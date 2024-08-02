package rezero;

import java.util.Spliterator;
import java.util.function.Consumer;

public class CancellableSpliterator<T> implements Spliterator<T> {
  Spliterator<T> wrappedSpliterator;
  Sink<T> sink;

  CancellableSpliterator(Spliterator<T> spliterator, Sink<T> sink) {
    this.wrappedSpliterator = spliterator;
    this.sink = sink;
  }

  @Override
  public boolean tryAdvance(Consumer<? super T> action) {
    return wrappedSpliterator.tryAdvance(action) && !sink.cancellationRequested();
  }

  @Override
  public Spliterator<T> trySplit() {
    return wrappedSpliterator.trySplit();
  }

  @Override
  public long estimateSize() {
    return wrappedSpliterator.estimateSize();
  }

  @Override
  public int characteristics() {
    return wrappedSpliterator.characteristics();
  }
}
