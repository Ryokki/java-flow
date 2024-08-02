package rezero;

interface TerminateSink<T, R> extends Sink<T> {

  // called after pipeline evaluated
  R getResult();

  @Override
  default void begin(long size) {}

  @Override
  default void end() {}
}
