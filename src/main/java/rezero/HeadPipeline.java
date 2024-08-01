package rezero;

import java.util.Spliterator;
import java.util.function.Supplier;

public class HeadPipeline<T> extends AbstractPipeline<T, T> {
  @Override
  Sink<T> wrapSink(Sink<T> downSink) {
    return new Sink<T>() {
      @Override
      public void begin(long size) {
        downSink.begin(size);
      }

      @Override
      public void accept(T value) {
        downSink.accept(value);
      }

      @Override
      public void end() {
        downSink.end();
      }
    };
  }

  HeadPipeline(Supplier<? extends Spliterator<?>> sourceSupplier) {
    super(sourceSupplier);
  }

  HeadPipeline(Spliterator<?> sourceSupplier) {
    super(() -> sourceSupplier);
  }
}
