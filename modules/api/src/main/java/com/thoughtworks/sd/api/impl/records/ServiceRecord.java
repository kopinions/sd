package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.impl.Record;

import java.util.HashMap;
import java.util.Map;

public class ServiceRecord implements Record, Service {
    private String ip;
    private String host;
    private Integer port;
    private String uri;
    private String name;
    private Status status;
    private Map<String, Object> credential;

    public ServiceRecord(Map<String, Object> data) {
        name = String.valueOf(data.get("name"));
        uri = String.valueOf(data.get("uri"));
        port = Integer.valueOf(String.valueOf(data.get("port")));
        host = String.valueOf(data.get("host"));
        ip = String.valueOf(data.get("ip"));
        credential = (Map<String, Object>) data.get("credential");
        status = Status.RUNNING;
    }

    @Override
    public Map<String, Object> toJson() {
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", name);
        serviceData.put("uri", uri);
        serviceData.put("ip", ip);
        serviceData.put("host", host);
        serviceData.put("port", port);
        serviceData.put("credential", credential);
        return serviceData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        this.status = Status.RUNNING;
    }

    @Override
    public void delete() {
        status = Status.DELETED;
    }

    @Override
    public boolean isRunning() {
        return status == Status.RUNNING;
    }
}
