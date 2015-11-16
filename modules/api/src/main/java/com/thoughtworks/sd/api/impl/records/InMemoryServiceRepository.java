package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class InMemoryServiceRepository implements ServiceRepository {
    private Map<String, Service> services = new HashMap<>();

    @Inject
    public String mesosDnsEntryPoint;

    @Override

    public Optional<Service> findByName(String name) {
        return Optional.ofNullable(services.get(name));
    }

    @Override
    public Service create(Map<String, Object> data) {

        Client client = ClientBuilder.newClient();
        client.register(new LoggingFilter(Logger.getLogger("INmeme"), true));
        Response name = client.target(mesosDnsEntryPoint + "/v1/services/_" + String.valueOf(data.get("name")) + "._tcp.servicedashboard.mesos").request().get();
        List<Map<String, Object>> bindings = name.readEntity(List.class);
        Map<String, Object> stringObjectMap = bindings.get(0);
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", String.valueOf(data.get("name")));
        serviceData.put("port", stringObjectMap.get("port"));
        serviceData.put("host", stringObjectMap.get("host"));
        serviceData.put("ip", stringObjectMap.get("ip"));
        serviceData.put("credential", new HashMap<String, Object>() {{
            put("password", "password");
            put("username", "root");
        }});
        ServiceRecord serviceRecord = new ServiceRecord(serviceData);
        services.put(String.valueOf(data.get("name")), serviceRecord);
        return serviceRecord;
    }
}
