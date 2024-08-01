package rezero;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

  // here functions

  @Override
  public Flow<E_OUT> filter(Predicate<? super E_OUT> predicate) {
    return new MidPipeline<>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<E_OUT> downSink) {
        return new Sink<>() {
          @Override
          public void begin(long size) {
            downSink.begin(size);
          }

          @Override
          public void accept(E_OUT value) {
            if (predicate.test(value)) {
              downSink.accept(value);
            }
          }

          @Override
          public void end() {
            downSink.end();
          }
        };
      }
    };
  }

  @Override
  // 只有参数中可以?吗
  public <R> Flow<R> map(Function<? super E_OUT, ? extends R> mapper) {
    return new MidPipeline<>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<R> downSink) {
        return new Sink<>() {
          @Override
          public void begin(long size) {
            downSink.begin(size);
          }

          @Override
          public void accept(E_OUT value) {
            downSink.accept(mapper.apply(value));
          }

          @Override
          public void end() {
            downSink.end();
          }
        };
      }
    };
  }

  @Override
  public Flow<E_OUT> sort(Comparator<? super E_OUT> comparator) {
    return new MidPipeline<>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<E_OUT> downSink) {
        return new Sink<>() {
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
  }

  @Override
  public long count() {
    // mapStream.count()
    return evaluate(
        new TerminateSink<>() {
          private long cnt;

          @Override
          public void begin(long size) {}

          @Override
          public void accept(E_OUT value) {
            cnt++;
          }

          @Override
          public void end() {}

          @Override
          public Long getResult() {
            return cnt;
          }
        });
  }

  @SuppressWarnings("unchecked")
  private <R> R evaluate(TerminateSink<E_OUT, R> terminateSink) {
    Sink headSink = generateSinkChain(terminateSink);

    Spliterator spliterator = sourceSupplier.get();
    headSink.begin(spliterator.estimateSize());
    spliterator.forEachRemaining(headSink::accept);
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
    while (currentPipeline != null) {
      downSink = currentPipeline.wrapSink(downSink);
      currentPipeline = currentPipeline.previousStage;
    }
    return downSink;
  }
}
