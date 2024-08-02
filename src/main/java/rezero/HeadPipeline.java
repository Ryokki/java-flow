package rezero;

import java.util.Spliterator;
import java.util.function.Supplier;

public class HeadPipeline<T> extends AbstractPipeline<T, T> {
  @Override
  Sink<T> wrapSink(Sink<T> downSink) {
    return new ChainedSink<>(downSink) {
      @Override
      public void accept(T value) {
        downStream.accept(value);
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
