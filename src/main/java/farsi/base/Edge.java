package farsi.base;

import farsi.experiment.EdgeTemplate;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.power.PowerMeasurement;
import org.cloudsimplus.power.models.PowerModelHostSimple;
import org.cloudsimplus.provisioners.PeProvisionerSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Setter
@Getter
public class Edge {

    private Long id;
    private Host host;
    private Vm vm;

    private int pes;
    private int mips;
    private double staticPower;
    private double dynamicPower;
    private long ram; // in MB

    private static final long BAND_WIDTH = 1000; // in Mbps
    private static final long STORAGE = 1000_000; // in MB
    private static final double HOST_START_UP_DELAY = 5;
    private static final double HOST_SHUT_DOWN_DELAY = 3;
    private static final double HOST_START_UP_POWER = 5;
    private static final double HOST_SHUT_DOWN_POWER = 3;

    public Edge(EdgeTemplate template) {
        this.id = template.getId();
        this.pes = template.getPes();
        this.mips = template.getMips();
        this.staticPower = template.getStaticPower();
        this.dynamicPower = template.getDynamicPower();
        this.ram = template.getRam();

        createHost(id);
        createVm(id);
    }

    private void createVm(Long id) {
        this.vm = new VmSimple(id, mips, pes);
        vm.setRam(ram).setBw(BAND_WIDTH).setSize(STORAGE).enableUtilizationStats();
        vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
        host.createVm(vm);
    }

    private void createHost(Long id) {
        List<Pe> peList = new ArrayList<>(pes);

        for (int i = 0; i < pes; i++) {
            peList.add(new PeSimple(mips));
        }

        var vmScheduler = new VmSchedulerSpaceShared();

        this.host = new HostSimple(ram, BAND_WIDTH, STORAGE, peList);
        host.setStartupDelay(HOST_START_UP_DELAY).setShutDownDelay(HOST_SHUT_DOWN_DELAY);

        var powerModel = new PowerModelHostSimple(staticPower + dynamicPower, staticPower);
        powerModel.setStartupPower(HOST_START_UP_POWER).setShutDownPower(HOST_SHUT_DOWN_POWER);

        host.setId(id);
        host.setVmScheduler(vmScheduler);
        host.setPowerModel(powerModel);
        host.enableUtilizationStats();
    }

    public double calculatingEnergy(Task task, List<Task> tasks) {
        double time = calculatingCompletionTime(task, tasks);
        double utilization = (double) task.getPES() / this.getPes();
        double power = this.getHost().getPowerModel().getPower(utilization);

        return power * time;
    }

    public double calculatingCompletionTime(Task task, List<Task> tasks) {
        // Space Shared policy with pes = 1 for all tasks
        double[] lastIdleTimes = new double[pes];
        double lastIdleTime = 0;

        for (int i = 0; i < tasks.size(); i++) {
            double executionTime = ((double) tasks.get(i).getLength() / this.mips);
            boolean select = false;
            double candidateLastIdleTime = Double.MAX_VALUE;

            for (int j = 0; j < lastIdleTimes.length; j++) {
                if (candidateLastIdleTime > lastIdleTimes[j])
                    candidateLastIdleTime = lastIdleTimes[j];
                if (lastIdleTime == lastIdleTimes[j]) {
                    lastIdleTimes[j] += executionTime;
                    select = true;
                    if (Objects.equals(task.getId(), tasks.get(i).getId()))
                        return lastIdleTimes[j];
                    break;
                }
            }
            if (!select) {
                i -= 1;
                lastIdleTime = candidateLastIdleTime;
            }
        }

        return 0;
    }
}
