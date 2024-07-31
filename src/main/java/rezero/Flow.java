package rezero;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Flow<T> {
  Flow<T> filter(Predicate<? super T> predicate);

  // 不理解这里的泛型
  <R> Flow<R> map(Function<? super T, ? extends R> mapper);

  int count();
}
