package rezero;

// 这个的好处是可以有下面三个方法的默认实现
public abstract class ChainedSink<E_IN, E_OUT> implements Sink<E_IN> {
  protected Sink<E_OUT> next;

  @Override
   public void begin(long size) {
    next.begin(size);
  }

  @Override
  public void end() {
    next.end();
  }

  @Override
  public boolean cancellationRequested() {
    return next.cancellationRequested();
  }
}
