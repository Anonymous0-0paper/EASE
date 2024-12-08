package farsi.base;

import farsi.experiment.TaskTemplate;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;


@Setter
@Getter
public class Task {

    private Long id;
    private UtilizationModel utilization;
    private Cloudlet cloudlet;

    private double deadline;
    private int length;
    private final int PES = 1; // don't change this, because pes of task affect the Edge:calculatingCompletionTime
    private final int FILE_SIZE = 1024;
    private final int OUTPUT_SIZE = 1024;

    public Task(TaskTemplate template) {
        this.id = template.getId();
        this.deadline = template.getDeadline();
        this.length = template.getLength();
        this.utilization = new UtilizationModelDynamic(0);;
        createCloudlet(id);
    }

    private void createCloudlet(Long id) {
        this.cloudlet = new CloudletSimple(id, length, PES)
                .setFileSize(FILE_SIZE)
                .setOutputSize(OUTPUT_SIZE)
                .setUtilizationModelCpu(new UtilizationModelFull())
                .setUtilizationModelRam(utilization)
                .setUtilizationModelBw(utilization);
    }
}
