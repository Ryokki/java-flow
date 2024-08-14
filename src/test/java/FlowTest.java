import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rezero.Utils;

public class FlowTest {
  @Test
  public void testCount() {
    Predicate<Number> predicate = number -> number.intValue() % 2 == 0;
    long cnt =
        Utils.flow(List.of(1, 2, 4, 3, 5, 6))
            .filter(predicate)
            .map(i -> String.valueOf(i * 10))
            .count();
    Assertions.assertEquals(3, cnt);
  }

  @Test
  public void testSort() {
    Predicate<Number> predicate = number -> number.intValue() % 2 == 0;
    long cnt =
        Utils.flow(List.of(1, 2, 4, 3, 5, 6))
            .filter(predicate)
            .map(i -> String.valueOf(i * 10))
            .sort(String::compareTo)
            .count();
    Assertions.assertEquals(3, cnt);
    // need toList function to assert
  }

  @Test
  public void testFindAny() {
    int any =
        Utils.flow(List.of(1, 4, 2, 3, 5, 6))
            .filter(number -> number.intValue() % 2 == 0)
            .sort(Comparator.comparingInt(a -> a))
            .findAny()
            .get();
    Assertions.assertEquals(4, any);
  }

  @Test
  public void testCollectToList() {
    ArrayList<Integer> result =
        Utils.flow(List.of(1, 4, 2, 3, 5, 6))
            .collect(ArrayList::new, (list, element) -> list.add(element));
    Assertions.assertEquals(6, result.size());
  }
}
