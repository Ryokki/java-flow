package rezero;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <E_IN> previous output, used in wrapSink
 * @param <E_OUT> current output such as List.of(1,2,3).stream().map(String::valueOf), E_IN = int,
 *     E_OUT = String.
 */
public abstract class AbstractPipeline<E_IN, E_OUT> implements Flow<E_OUT> {
  private final Supplier<? extends Spliterator<?>> sourceSupplier;
  private final AbstractPipeline<?, E_IN> previousStage;
  private int flag = 0;
  protected Predicate<Integer> ignoredPredicate = flag -> false;

  abstract Sink<E_IN> wrapSink(Sink<E_OUT> downSink);

  // constructors
  AbstractPipeline(Supplier<? extends Spliterator<?>> sourceSupplier) {
    this.previousStage = null;
    this.sourceSupplier = sourceSupplier;
  }

  AbstractPipeline(AbstractPipeline<?, E_IN> upstream) {
    this.previousStage = upstream;
    this.sourceSupplier = upstream.sourceSupplier;
  }

  @Override
  public Flow<E_OUT> filter(Predicate<? super E_OUT> predicate) {
    return new MidPipeline<>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<E_OUT> downSink) {
        return new ChainedSink<>(downSink) {
          @Override
          public void accept(E_OUT value) {
            if (predicate.test(value)) {
              downStream.accept(value);
            }
          }
        };
      }
    };
  }

  @Override
  public <R> Flow<R> map(Function<? super E_OUT, ? extends R> mapper) {
    return new MidPipeline<>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<R> downSink) {
        return new ChainedSink<>(downSink) {
          @Override
          public void accept(E_OUT value) {
            downStream.accept(mapper.apply(value));
          }
        };
      }
    };
  }

  @Override
  public Flow<E_OUT> sort(Comparator<? super E_OUT> comparator) {
    MidPipeline<E_OUT, E_OUT> midPipeline =
        new MidPipeline<>(this) {
          @Override
          Sink<E_OUT> wrapSink(Sink<E_OUT> downSink) {
            return new ChainedSink<>(downSink) {
              private List<E_OUT> list;

              @Override
              public void begin(long size) {
                list = size != -1 ? new ArrayList<>((int) size) : new ArrayList<>();
              }

              @Override
              public void accept(E_OUT value) {
                list.add(value);
              }

              @Override
              public void end() {
                list.sort(comparator);
                downSink.begin(list.size());
                list.forEach(downSink::accept);
                downSink.end();
              }
            };
          }
        };
    midPipeline.ignoredPredicate = flag -> (flag | FlowFlag.FIND_ANY.getMask()) != 0;
    return midPipeline;
  }

  @Override
  public long count() {
    return evaluate(
        new TerminateSink<>() {
          private long cnt;

          @Override
          public void accept(E_OUT value) {
            cnt++;
          }

          @Override
          public Long getResult() {
            return cnt;
          }
        });
  }

  @Override
  public Optional<E_OUT> findAny() {
    setFlags(FlowFlag.FIND_ANY, FlowFlag.CANCELLABLE);
    return evaluate(
        new TerminateSink<>() {
          private E_OUT result;
          boolean accepted = false;

          @Override
          public void accept(E_OUT value) {
            result = value;
            accepted = true;
          }

          @Override
          public boolean cancellationRequested() {
            return accepted;
          }

          @Override
          public Optional<E_OUT> getResult() {
            return Optional.ofNullable(result);
          }
        });
  }

  @SuppressWarnings("unchecked")
  private <R> R evaluate(TerminateSink<E_OUT, R> terminateSink) {
    Sink headSink = generateSinkChain(terminateSink);

    Spliterator spliterator = sourceSupplier.get();
    headSink.begin(spliterator.estimateSize());

    if (isFlagSet(FlowFlag.CANCELLABLE)) {
      new CancellableSpliterator<>(spliterator, headSink).forEachRemaining(headSink::accept);
    } else {
      spliterator.forEachRemaining(headSink::accept);
    }
    headSink.end();

    return terminateSink.getResult();
  }

  @SuppressWarnings("unchecked")
  private Sink<?> generateSinkChain(TerminateSink terminateSink) {
    // 组装成一条Sink链条: headSink对象中需要存filterSink, filterSink存mapSink,
    // mapSink存countSink, countSink跑完结束
    // 所以就在mapSink的wrap传入countSink，filterSink传入mapSink，headSink传入filterSink
    AbstractPipeline currentPipeline = this;
    Sink<?> downSink = terminateSink;
    for (; currentPipeline != null; currentPipeline = currentPipeline.previousStage) {
      if (!currentPipeline.toBeIgnored()) {
        downSink = currentPipeline.wrapSink(downSink);
      }
    }
    return downSink;
  }

  private boolean toBeIgnored() {
    return ignoredPredicate.test(getHead().flag);
  }

  private AbstractPipeline getHead() {
    AbstractPipeline current = this;
    while (current.previousStage != null) {
      current = current.previousStage;
    }
    return current;
  }

  // 一个stream是由一堆pipeline组成的，得找个地方存Flag, 那就找head吧! 所以这里加个head字段 TODO
  private void setFlags(FlowFlag... flags) {
    for (FlowFlag flowFlag : flags) {
      getHead().flag |= 1 << flowFlag.getMask();
    }
  }

  private boolean isFlagSet(FlowFlag flowFlag) {
    int i = 1 & (1 << 2);
    return (getHead().flag & (1 << flowFlag.getMask())) != 0;
  }
}
