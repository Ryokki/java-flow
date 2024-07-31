package rezero;

import java.util.Collection;

public class Utils {
  public static <T> Flow<T> flow(Collection<T> collection) {
    return new HeadPipeline<>(collection.spliterator());
  }
}
