package farsi.experiment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class EdgeTemplate {

    private Long id;
    private int pes;
    private int mips;
    private double staticPower;
    private double dynamicPower;
    private long ram;
}
