package copy;

import copy.step.HeadStep;
import copy.step.IntermediateStep;

import java.util.Collection;
import java.util.List;

public class Flow<T> {
  private HeadStep<T> headStep;
  private List<IntermediateStep<T>> intermediateSteps;


//  public static <E> Flow<E> from(Collection<E> collection) {
//    return new Flow(
//        new HeadStep(    collection.spliterator()), null);
//  }
}
