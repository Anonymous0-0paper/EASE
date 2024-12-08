package farsi.base;

import farsi.experiment.Experiment;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Algorithm {

    private CloudSimPlus simulation;
    private List<Edge> edges;
    private List<Task> tasks;
    private Datacenter datacenter;
    private Broker broker;

    private static final int SCHEDULING_INTERVAL = 10;

    public Algorithm(List<Edge> edges, List<Task> tasks) {
        this.edges = edges;
        this.tasks = tasks;
        List<Host> hosts = edges.stream().map(Edge::getHost).toList();

        this.simulation = new CloudSimPlus();
        this.datacenter = new DatacenterSimple(simulation, hosts)
                .setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    public void createBroker(Map<Long, Long> mapper) {

        List<Vm> vms = this.edges.stream().map(Edge::getVm).toList();
        List<Cloudlet> cloudlets = this.tasks.stream().map(Task::getCloudlet).toList();

        this.broker = new Broker(this.simulation, mapper);
        this.broker.submitVmList(vms);
        this.broker.submitCloudletList(cloudlets);
    }

    public void run() {
        this.simulation.start();
    }

    public Experiment output() {

        double totalTime = broker.getShutdownTime() - broker.getStartTime();
        int taskCount = getTasks().size();

        int successTaskCount = 0;
        for (Task task : getTasks()) {
            if (task.getCloudlet().getFinishTime() <= task.getDeadline())
                successTaskCount += 1;
        }

        double[] utilization = new double[edges.size()];
        double energy = 0;
        for (int i = 0; i < getEdges().size(); i++) {
            var powerModel = getEdges().get(i).getHost().getPowerModel();

            double vmUtilization = getEdges().get(i).getVm().getCpuUtilizationStats().getMean();
            double power = powerModel.getPower(vmUtilization);

            utilization[i] = vmUtilization;
            energy += power * totalTime;
        }

        return Experiment.builder()
                .energy(energy)
                .totalTime(totalTime)
                .taskCount(taskCount)
                .successTaskCount(successTaskCount)
                .utilization(utilization)
                .build();
    }
}
