package com.thoughtworks.sd.api;

import org.apache.mesos.SchedulerDriver;

public class MesosDriverHolder {
    public static SchedulerDriver getDriver() {
        return driver;
    }

    public static SchedulerDriver driver;

}
