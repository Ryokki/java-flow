package rezero;

interface TerminateSink<T, R> extends Sink<T> {

  // called after pipeline evaluated
  R getResult();
}
