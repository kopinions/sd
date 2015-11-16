package com.thoughtworks.sd.api.core;

public interface Service {
    String getName();

    void start();

    void delete();

    boolean isRunning();

    enum Status {
        DELETED, NEW, RUNNING
    }
}
