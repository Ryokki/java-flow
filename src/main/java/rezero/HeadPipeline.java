package rezero;

import java.util.Spliterator;
import java.util.function.Supplier;

public class HeadPipeline<E_IN, E_OUT> extends AbstractPipeline<E_IN, E_OUT> {
  HeadPipeline(Supplier<? extends Spliterator<?>> sourceSupplier) {
    super(sourceSupplier);
  }

  HeadPipeline(Spliterator<?> sourceSupplier) {
    super(() -> sourceSupplier);
  }

  public Sink<E_IN> wrapSink(Sink<E_OUT> sink) {
    throw new IllegalStateException();
  }
}
