package rezero;

public abstract class MidPipeline<E_IN, E_OUT> extends AbstractPipeline<E_IN, E_OUT> {
  MidPipeline(AbstractPipeline upstream) {
    super(upstream);
  }
}
