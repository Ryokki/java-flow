package rezero;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    long cnt =
        Utils.flow(List.of(1, 2, 4, 3, 5, 6))
            .filter(i -> i % 2 == 0)
            .map(i -> String.valueOf(i * 10))
            .count();
    System.out.println("count = " + cnt);
  }
}
