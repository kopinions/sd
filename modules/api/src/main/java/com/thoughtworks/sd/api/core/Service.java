package com.thoughtworks.sd.api.core;

public interface Service {
    String getName();

    void start();

    enum Status {
        RUNNING
    }
}
