package rezero;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    try {
      Flow<Integer> flow = Utils.flow(List.of(1, 2, 4, 3, 5, 6));
      Flow<Integer> flowWithFilter = flow.filter(i -> i % 2 == 0);
      Flow<String> flowWithFilterAndMap = flowWithFilter.map(i -> String.valueOf(i * 10));
      long count = flowWithFilterAndMap.count();
      System.out.println("count = " + count);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }
}
