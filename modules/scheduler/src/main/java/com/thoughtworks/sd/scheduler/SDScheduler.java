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
            TaskID taskId = TaskID.newBuilder().setValue(Integer.toString(launchedTasks++)).build();

            ContainerInfo.DockerInfo.Builder dockerInfo = ContainerInfo.DockerInfo
                    .newBuilder()
                    .setImage("localhost:5000/myfirstimage")
                    .setNetwork(ContainerInfo.DockerInfo.Network.HOST);

            ContainerInfo.Builder builderForValue = ContainerInfo.newBuilder()
                    .setType(ContainerInfo.Type.DOCKER)
                    .setDocker(dockerInfo.build());

            Protos.TaskInfo task = Protos.TaskInfo
                    .newBuilder()
                    .setName("task " + taskId.getValue())
                    .setTaskId(taskId)
                    .setSlaveId(offer.getSlaveId())
                    .addResources(
                            Protos.Resource.newBuilder().setName("cpus").setType(Protos.Value.Type.SCALAR)
                                    .setScalar(Protos.Value.Scalar.newBuilder().setValue(0.2)))
                    .addResources(
                            Protos.Resource.newBuilder().setName("mem").setType(Protos.Value.Type.SCALAR)
                                    .setScalar(Protos.Value.Scalar.newBuilder().setValue(128)))
                    .setCommand(Protos.CommandInfo.newBuilder().setUser("root").setShell(false).build())
                    .setContainer(builderForValue)
                    .build();

            Protos.Filters filters = Protos.Filters.newBuilder().setRefuseSeconds(1).build();

            driver.launchTasks(Arrays.asList(offer.getId()), Arrays.asList(task), filters);
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
                .setName("Test Framework (Java)");

        if (System.getenv("MESOS_CHECKPOINT") != null) {
            System.out.println("Enabling checkpoint for the framework");
            frameworkBuilder.setCheckpoint(true);
        }

        boolean implicitAcknowledgements = true;

        if (System.getenv("MESOS_EXPLICIT_ACKNOWLEDGEMENTS") != null) {
            System.out.println("Enabling explicit acknowledgements for status updates");
            implicitAcknowledgements = false;
        }

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
            frameworkBuilder.setPrincipal("test-framework-java");

            driver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), args[0], implicitAcknowledgements);
        }


        new ApiModule().run();

        int status = driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;

        // Ensure that the driver process terminates.
        driver.stop();
        Thread.sleep(500);

        System.exit(status);
    }
}
