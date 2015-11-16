package com.thoughtworks.sd.scheduler;

import com.thoughtworks.sd.api.core.ServiceRepository;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;

public class OfferProcessor {
    private int launchedTasks;

    public OfferProcessor() {
    }

    void process(SchedulerDriver driver, Protos.Offer offer) {
        if (launchedTasks < 2) {
            launchedTasks++;

            if (!offerMatched(offer)) {
                return;
            }

            Protos.Resource ports = offer.getResourcesList().stream()
                    .filter(r -> Objects.equals(r.getName(), "ports")).findFirst().get();

            Protos.TaskInfo task = buildTaskInfo(offer, ports.getRanges());

            Protos.Filters filters = Protos.Filters.newBuilder().setRefuseSeconds(1).build();

            driver.launchTasks(asList(offer.getId()), asList(task), filters);
        } else {
            driver.declineOffer(offer.getId());
        }
    }

    private boolean offerMatched(Protos.Offer offer) {
        boolean fulfilled = true;

        Protos.Resource cpu = offer.getResourcesList().stream()
                .filter(r -> Objects.equals(r.getName(), "cpus")).findFirst().get();
        if (cpu.getScalar().getValue() < 1) {
            fulfilled = false;
        }


        Protos.Resource memory = offer.getResourcesList().stream()
                .filter(r -> Objects.equals(r.getName(), "mem")).findFirst().get();

        if (memory.getScalar().getValue() < 500) {
            fulfilled = false;
        }

        Protos.Resource disk = offer.getResourcesList().stream()
                .filter(r -> Objects.equals(r.getName(), "disk")).findFirst().get();


        if (disk.getScalar().getValue() < 1024) {
            fulfilled = false;
        }

        Protos.Resource ports = offer.getResourcesList().stream()
                .filter(r -> Objects.equals(r.getName(), "ports")).findFirst().get();

        if (!ports.hasRanges()) {
            fulfilled = false;
        }

        return fulfilled;
    }

    private Protos.TaskInfo buildTaskInfo(Protos.Offer offer, Protos.Value.Ranges ranges) {
        Protos.Value.Range range = ranges.getRange(0);
        Protos.ContainerInfo.DockerInfo.Builder dockerInfo = Protos.ContainerInfo.DockerInfo
                .newBuilder()
                .setImage("mysql")
                .setNetwork(Protos.ContainerInfo.DockerInfo.Network.BRIDGE)
                .addPortMappings(0, Protos.ContainerInfo.DockerInfo.PortMapping.newBuilder().setContainerPort(3306)
                        .setProtocol("tcp").setHostPort((int) range.getBegin()));

        Protos.ContainerInfo.Builder builderForValue = Protos.ContainerInfo.newBuilder()
                .setType(Protos.ContainerInfo.Type.DOCKER)
                .setDocker(dockerInfo.build());


        return Protos.TaskInfo
                .newBuilder()
                .setName("mysql")
                .setTaskId(Protos.TaskID.newBuilder().setValue("sd_" + UUID.randomUUID().toString()))
                .setSlaveId(offer.getSlaveId())
                .addResources(
                        Protos.Resource.newBuilder().setName("cpus").setType(Protos.Value.Type.SCALAR)
                                .setScalar(Protos.Value.Scalar.newBuilder().setValue(1)))
                .addResources(
                        Protos.Resource.newBuilder().setName("mem").setType(Protos.Value.Type.SCALAR)
                                .setScalar(Protos.Value.Scalar.newBuilder().setValue(500)))
                .addResources(
                        Protos.Resource.newBuilder().setName("disk").setType(Protos.Value.Type.SCALAR)
                                .setScalar(Protos.Value.Scalar.newBuilder().setValue(2000)))
                .addResources(Protos.Resource.newBuilder()
                                .setName("ports")
                                .setType(Protos.Value.Type.RANGES)
                                .setRanges(Protos.Value.Ranges.newBuilder().addRange(Protos.Value.Range.newBuilder().setBegin(range.getBegin()).setEnd(range.getBegin())))
                )
                .setCommand(Protos.CommandInfo.newBuilder().setUser("root").setShell(false)
                        .setEnvironment(Protos.Environment.newBuilder().addVariables(Protos.Environment.Variable.newBuilder().setName("MYSQL_ROOT_PASSWORD").setValue("password")))
                        .build())
                .setContainer(builderForValue)
                .setDiscovery(Protos.DiscoveryInfo.newBuilder()
                                .setVisibility(Protos.DiscoveryInfo.Visibility.EXTERNAL)
                )
                .build();
    }
}
