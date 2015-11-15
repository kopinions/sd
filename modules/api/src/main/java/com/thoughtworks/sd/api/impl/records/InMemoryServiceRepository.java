package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryServiceRepository implements ServiceRepository {
    private Map<String, Service> services = new HashMap<>();
    @Override

    public Optional<Service> findByName(String name) {
        return Optional.ofNullable(services.get(name));
    }

    @Override
    public Service create(Map<String, Object> data) {
        ServiceRecord serviceRecord = new ServiceRecord(data);
        services.put(String.valueOf(data.get("name")), serviceRecord);
        return serviceRecord;
    }
}
