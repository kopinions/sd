package com.thoughtworks.sd.api.core;

import java.util.Map;
import java.util.Optional;

public interface ServiceRepository {
    Optional<Service> findByName(String name);

    Service create(Map<String, Object> data);
}
