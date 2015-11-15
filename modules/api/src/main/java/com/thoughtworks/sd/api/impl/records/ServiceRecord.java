package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.impl.Record;

import java.util.HashMap;
import java.util.Map;

public class ServiceRecord implements Record, Service {
    private String uri;
    private String name;

    public ServiceRecord(Map<String, Object> data) {
        name = String.valueOf(data.get("name"));
        uri = String.valueOf(data.get("uri"));
    }

    @Override
    public Map<String, Object> toJson() {
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", name);
        serviceData.put("uri", uri);
        return serviceData;
    }

    @Override
    public String getName() {
        return name;
    }
}
