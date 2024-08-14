package rezero;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Flow<T> {
  /**
   * 为什么这里要super? Predicate<Number> predicate = number -> number.equals(1). int类型的stream,
   * .filter(predicate)这样会炸，因为Predicate<Number>不是Predicate<Integer> ... 这么bt？ 语言缺陷？
   */
  Flow<T> filter(Predicate<? super T> predicate);

  /**
   * 为什么R要extends？ Function<Number, Long> function = number -> number.longValue(); Flow<Number>
   * longStream = Utils.flow(List.of(1, 2, 4, 3, 5, 6)).map(function) 如果不写extends,
   * mapper的value是什么类型，Flow就必须是什么类型。这里mapper的value是Long类型，但是想返回Flow<Number>，这就不合法了 如果写了extends,
   * Flow的泛型是value的super，即Long的super都行 本质上是，当使用同一个泛型符号时，他们就代表同一个类型，不能是继承或实现关系，就是完全是同个类型. (这看起来挺合理的～
   * 规定上下界)
   */
  <R> Flow<R> map(Function<? super T, ? extends R> mapper);

  Flow<T> sort(Comparator<? super T> comparator);

  long count();

  /** 除非流具有遇到顺序，在这种情况下findFirst()返回第一个元素，而findAny()将返回任何元素 (跳过排序?) */
  Optional<T> findAny();

  <C extends Collection<T>> C collect(Supplier<C> supplier, BiConsumer<C, T> consumer);
}
