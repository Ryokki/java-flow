package rezero;

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
  private final AbstractPipeline previousStage;
  private AbstractPipeline nextStage;

  abstract Sink<E_IN> wrapSink(Sink<E_OUT> downSink);

  // constructors
  AbstractPipeline(Supplier<? extends Spliterator<?>> sourceSupplier) {
    this.previousStage = null;
    this.nextStage = null;
    this.sourceSupplier = sourceSupplier;
  }

  AbstractPipeline(AbstractPipeline upstream) {
    upstream.nextStage = this;
    this.previousStage = upstream;
    this.sourceSupplier = upstream.sourceSupplier;
  }

  // here functions

  @Override
  public Flow<E_OUT> filter(Predicate<? super E_OUT> predicate) {
    return new MidPipeline<E_OUT, E_OUT>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<E_OUT> downSink) {
        return new Sink<>() {
          @Override
          public void begin(long size) {}

          @Override
          public void accept(E_OUT value) {
            if (predicate.test(value)) {
              downSink.accept(value);
            }
          }

          @Override
          public void end() {}
        };
      }
    };
  }

  @Override
  // 只有参数中可以?吗
  public <R> Flow<R> map(Function<? super E_OUT, ? extends R> mapper) {
    return new MidPipeline<E_OUT, R>(this) {
      @Override
      Sink<E_OUT> wrapSink(Sink<R> downSink) {
        return new Sink<E_OUT>() {
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
  public long count() {
    // mapStream.count()
    return evaluate(
        new TerminateSink<E_OUT, Long>() {
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

  private <R> R evaluate(TerminateSink<E_OUT, R> terminateSink) {
    Sink headSink = generateSinkChain(terminateSink);

    Spliterator spliterator = sourceSupplier.get();
    headSink.begin(spliterator.estimateSize());
    spliterator.forEachRemaining(element -> headSink.accept(element));
    headSink.end();

    return terminateSink.getResult();
  }

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
