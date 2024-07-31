package rezero;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    System.out.println("e");
    Flow<Integer> flow = Utils.flow(List.of(1, 2, 4, 3, 5, 6));
    Flow<Integer> flowWithFilter = flow.filter(i -> i % 2 == 0);
    Flow<String> flowWithFilterAndMap = flowWithFilter.map(i -> String.valueOf(i * 10));
    System.out.println(flowWithFilterAndMap);
  }
}
