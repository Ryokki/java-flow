package rezero;

// 这个的好处是可以有下面三个方法的默认实现
public abstract class ChainedSink<E_IN, E_OUT> implements Sink<E_IN> {
  protected Sink<E_OUT> downStream;

  ChainedSink(Sink<E_OUT> downStream) {
    this.downStream = downStream;
  }

  @Override
  public void begin(long size) {
    downStream.begin(size);
  }

  @Override
  public void end() {
    downStream.end();
  }

  @Override
  public boolean cancellationRequested() {
    return downStream.cancellationRequested();
  }
}
