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
    int result =
        Utils.flow(List.of(3, 2, 5, 6, 4, 1))
            .filter(number -> number.intValue() % 2 == 0)
            .findAny()
            .get();
    Assertions.assertEquals(2, result);
  }
}
