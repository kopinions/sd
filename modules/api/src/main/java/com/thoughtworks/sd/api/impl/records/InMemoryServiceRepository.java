package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.impl.MesosDns;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryServiceRepository implements ServiceRepository {
    private Map<String, Service> services = new HashMap<>();

    @Inject
    MesosDns mesosDns;

    @Override
    public Optional<Service> findByName(String name) {
        Service value = services.getOrDefault(name, null);
        if ((value == null
                || !value.isRunning())) {
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    @Override
    public Service create(Map<String, Object> data) {
        Map<String, Object> dnsInfo = mesosDns.getDnsInfo(data);
        if (dnsInfo.isEmpty()) {
            throw new RuntimeException("Unknow service find");
        }

        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("port", dnsInfo.get("port"));
        serviceData.put("host", dnsInfo.get("host"));
        serviceData.put("ip", dnsInfo.get("ip"));
        serviceData.put("uri", "jdbc:mysql://" + dnsInfo.get("host") + ":" + dnsInfo.get("port") + "/mysql?" + "user=root&password=password");

        serviceData.put("credential", new HashMap<String, Object>() {{
            put("password", "password");
            put("username", "root");
        }});
        serviceData.put("name", String.valueOf(data.get("name")));
        ServiceRecord serviceRecord = new ServiceRecord(serviceData);
        services.put(String.valueOf(data.get("name")), serviceRecord);
        return serviceRecord;
    }

}
