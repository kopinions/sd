package com.thoughtworks.sd.api.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MesosDns {
    @Inject
    @Named("dns_entry_point")
    public String dnsEntryPoint;

    public Map<String, Object> getDnsInfo(Map<String, Object> data) {
        Client client = ClientBuilder.newClient();
        client.register(new LoggingFilter(Logger.getLogger("mesos"), true));
        Response name = client.target(dnsEntryPoint + "/v1/services/_" + String.valueOf(data.get("name")) + "._tcp.sd.mesos").request().get();
        List<Map<String, Object>> bindings = name.readEntity(List.class);
        Map<String, Object> dnsInfo = bindings.get(0);
        if (String.valueOf(dnsInfo.get("service")).isEmpty()) {
            return new HashMap<>();
        }

        return dnsInfo;
    }
}
