package farsi.base;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerAbstract;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

import java.util.Map;

public class Broker extends DatacenterBrokerAbstract {

    private final Map<Long, Long> mapper;

    public Broker(CloudSimPlus simulation, Map<Long, Long> mapper) {
        super(simulation, "");
        this.mapper = mapper;
    }

    @Override
    protected Datacenter defaultDatacenterMapper(Datacenter datacenter, Vm vm) {
        if (this.getDatacenterList().isEmpty()) {
            throw new IllegalStateException("You don't have any Datacenter created.");
        }

        return this.getDatacenterList().getFirst();
    }

    @Override
    protected Vm defaultVmMapper(Cloudlet cloudlet) {
        if (mapper.containsKey(cloudlet.getId()))
            for (Vm vm : this.getVmExecList())
                if (vm.getId() == mapper.get(cloudlet.getId()))
                    return vm;

        return this.getVmExecList().getFirst();
    }
}
