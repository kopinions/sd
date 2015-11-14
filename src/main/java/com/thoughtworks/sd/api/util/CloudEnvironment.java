package com.thoughtworks.sd.api.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudEnvironment {
    private EnvironmentAccessor environment = new EnvironmentAccessor();
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static final TypeReference<List<Map<String, Object>>> SERVICE_TYPE = new TypeReference<List<Map<String, Object>>>() {
    };

    public List<Map<String, Object>> getServices() {
        String servicesString = getValue("DEP_SERVICES");
        if (servicesString == null || servicesString.length() == 0) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(servicesString, SERVICE_TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Object> getServiceDataByName(String name) {
        if (name == null || name.isEmpty()) {
            return new HashMap<>();
        }

        return getServices().stream().filter(s -> name.equals(s.get("name"))).findFirst().orElse(new HashMap<>());
    }

    public String getValue(String key) {
        return environment.getValue(key);
    }

    public static class EnvironmentAccessor {
        public String getValue(String key) {
            return System.getenv(key);
        }
    }
}
