package rezero;

public interface Sink<T> {
  void begin(long size);

  void accept(T value);

  void end();
}
