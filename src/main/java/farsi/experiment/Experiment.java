package farsi.experiment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Experiment {

    private String name;
    private double energy;
    private double totalTime;
    private double taskCount;
    private double successTaskCount;
    private double[] utilization;

    public void normalize(int iterations){
        this.energy = this.energy / iterations;
        this.totalTime = this.totalTime / iterations;
        this.taskCount = this.taskCount / iterations;
        this.successTaskCount = this.successTaskCount / iterations;

        for (int i = 0; i < utilization.length; i++) {
            this.utilization[i] = this.utilization[i] / iterations;
        }
    }

    public void add(Experiment another){
        this.energy += another.energy;
        this.totalTime += another.totalTime;
        this.taskCount += another.taskCount;
        this.successTaskCount += another.successTaskCount;

        for (int i = 0; i < utilization.length; i++) {
            this.utilization[i] += another.utilization[i];
        }
    }
}
