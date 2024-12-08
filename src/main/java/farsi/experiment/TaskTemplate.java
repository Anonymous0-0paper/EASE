package farsi.experiment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class TaskTemplate {

    private Long id;
    private int deadline;
    private int length;
}
