import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rezero.Utils;

public class FlowTest {
  @Test
  public void testCount() {
    long cnt =
        Utils.flow(List.of(1, 2, 4, 3, 5, 6))
            .filter(i -> i % 2 == 0)
            .map(i -> String.valueOf(i * 10))
            .count();
    Assertions.assertEquals(3, cnt);
    System.out.println("🎉🎉🎉🎉🎉 passed testCount");
  }

  @Test
  public void test2() {
    System.out.println("🎉🎉🎉🎉🎉 test2");
  }
}
