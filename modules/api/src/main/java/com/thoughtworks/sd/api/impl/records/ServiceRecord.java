package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.impl.Record;

import java.util.HashMap;
import java.util.Map;

public class ServiceRecord implements Record, Service {
    @Override
    public Map<String, Object> toJson() {
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", "mysql");
        return serviceData;
    }

}
