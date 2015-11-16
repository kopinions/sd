package com.thoughtworks.sd.scheduler;

import com.google.protobuf.ByteString;
import com.thoughtworks.sd.api.ApiModule;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Protos.ContainerInfo;
import org.apache.mesos.Protos.FrameworkInfo;
import org.apache.mesos.Protos.TaskID;
import org.apache.mesos.Protos.TaskStatus;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.asList;


public class SDScheduler implements Scheduler {
    private int launchedTasks;

    @Override
    public void registered(SchedulerDriver driver, Protos.FrameworkID frameworkId, Protos.MasterInfo masterInfo) {
        System.out.println("registered");
    }

    @Override
    public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo) {
        System.out.println("reregistered");
    }

    @Override
    public void resourceOffers(SchedulerDriver driver, List<Protos.Offer> offers) {

        for (Protos.Offer offer : offers) {
            if (launchedTasks < 2) {
                launchedTasks++;
                Protos.Resource cpu = offer.getResourcesList().stream()
                        .filter(r -> Objects.equals(r.getName(), "cpus")).findFirst().get();
                if (cpu.getScalar().getValue() < 1) {
                    continue;
                }


                Protos.Resource memory = offer.getResourcesList().stream()
                        .filter(r -> Objects.equals(r.getName(), "mem")).findFirst().get();

                if (memory.getScalar().getValue() < 500) {
                    continue;
                }

                Protos.Resource disk = offer.getResourcesList().stream()
                        .filter(r -> Objects.equals(r.getName(), "disk")).findFirst().get();

                if (disk.getScalar().getValue() < 1024) {
                    continue;
                }

                Protos.Resource resource = offer.getResourcesList().stream()
                        .filter(Protos.Resource::hasRanges).findFirst().get();
                Protos.Value.Ranges ranges = resource.getRanges();
                TaskID taskId = TaskID.newBuilder().setValue(Integer.toString(launchedTasks)).build();

                long begin = ranges.getRange(0).getBegin();
                ContainerInfo.DockerInfo.Builder dockerInfo = ContainerInfo.DockerInfo
                        .newBuilder()
                        .setImage("mysql")
                        .setNetwork(ContainerInfo.DockerInfo.Network.BRIDGE)
                        .addPortMappings(0, ContainerInfo.DockerInfo.PortMapping.newBuilder().setContainerPort(3306)
                                .setProtocol("tcp").setHostPort((int) begin));

                ContainerInfo.Builder builderForValue = ContainerInfo.newBuilder()
                        .setType(ContainerInfo.Type.DOCKER)
                        .setDocker(dockerInfo.build());

                Protos.TaskInfo task = Protos.TaskInfo
                        .newBuilder()
                        .setName("mysql")
                        .setTaskId(taskId)
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
                                        .setRanges(Protos.Value.Ranges.newBuilder().addRange(Protos.Value.Range.newBuilder().setBegin(ranges.getRange(0).getBegin()).setEnd(ranges.getRange(0).getBegin())))
                        )
                        .setCommand(Protos.CommandInfo.newBuilder().setUser("root").setShell(false)
                                .setEnvironment(Protos.Environment.newBuilder().addVariables(Protos.Environment.Variable.newBuilder().setName("MYSQL_ROOT_PASSWORD").setValue("password")))
                                .build())
                        .setContainer(builderForValue)
                        .setDiscovery(Protos.DiscoveryInfo.newBuilder()
                                        .setVisibility(Protos.DiscoveryInfo.Visibility.EXTERNAL)
                        )
                        .build();

                Protos.Filters filters = Protos.Filters.newBuilder().setRefuseSeconds(1).build();

                driver.launchTasks(asList(offer.getId()), asList(task), filters);
            } else {
                driver.declineOffer(offer.getId());
            }
        }
    }

    @Override
    public void offerRescinded(SchedulerDriver driver, Protos.OfferID offerId) {
        System.out.println("offerRescinded");
    }

    @Override
    public void statusUpdate(SchedulerDriver driver, TaskStatus status) {
        System.out.println("statusUpdate");
        System.out.println(status.toString());

        switch (status.getState()) {
            case TASK_RUNNING:
                System.out.println("runing");
                break;
            case TASK_FAILED:
            case TASK_FINISHED:
                System.out.println("fininsh");
                break;
        }
    }

    @Override
    public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data) {
        System.out.println("frameworkMessage");
    }

    @Override
    public void disconnected(SchedulerDriver driver) {
        System.out.println("disconnected");
    }

    @Override
    public void slaveLost(SchedulerDriver driver, Protos.SlaveID slaveId) {
        System.out.println("slaveLost");
    }

    @Override
    public void executorLost(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, int status) {
        System.out.println("executorLost");
    }

    @Override
    public void error(SchedulerDriver driver, String message) {
        System.out.println("error");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Error");
            System.exit(1);
        }

        FrameworkInfo.Builder frameworkBuilder = FrameworkInfo.newBuilder()
                .setUser("")
                .setName("servicedashboard");

        if (System.getenv("MESOS_CHECKPOINT") != null) {
            System.out.println("Enabling checkpoint for the framework");
            frameworkBuilder.setCheckpoint(true);
        }

        boolean implicitAcknowledgements = true;

        if (System.getenv("MESOS_EXPLICIT_ACKNOWLEDGEMENTS") != null) {
            System.out.println("Enabling explicit acknowledgements for status updates");
            implicitAcknowledgements = false;
        }

        ApiModule apiModule = new ApiModule();
        apiModule.run();

        Scheduler scheduler = new SDScheduler();

        MesosSchedulerDriver driver;
        if (System.getenv("MESOS_AUTHENTICATE") != null) {
            System.out.println("Enabling authentication for the framework");

            if (System.getenv("DEFAULT_PRINCIPAL") == null) {
                System.err.println("Expecting authentication principal in the environment");
                System.exit(1);
            }

            Protos.Credential.Builder credentialBuilder = Protos.Credential.newBuilder()
                    .setPrincipal(System.getenv("DEFAULT_PRINCIPAL"));

            if (System.getenv("DEFAULT_SECRET") != null) {
                credentialBuilder.setSecret(ByteString.copyFromUtf8(System.getenv("DEFAULT_SECRET")));
            }

            frameworkBuilder.setPrincipal(System.getenv("DEFAULT_PRINCIPAL"));

            driver = new MesosSchedulerDriver(
                    scheduler,
                    frameworkBuilder.build(),
                    args[0],
                    implicitAcknowledgements,
                    credentialBuilder.build());
        } else {
            frameworkBuilder.setPrincipal("servicedashboard");

            driver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), args[0], implicitAcknowledgements);
        }




        int status = driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;

        // Ensure that the driver process terminates.
        driver.stop();
        Thread.sleep(500);

        System.exit(status);
    }
}
