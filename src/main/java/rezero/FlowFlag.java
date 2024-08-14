package rezero;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FlowFlag {
  CANCELLABLE(0),
  FIND_ANY(1),
  ;

  private final int mask;
}
