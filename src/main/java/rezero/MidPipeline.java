package rezero;

public abstract class MidPipeline<E_IN, E_OUT> extends AbstractPipeline<E_IN, E_OUT> {
  MidPipeline(AbstractPipeline<?, E_IN> upstream) {
    super(upstream);
  }
}
