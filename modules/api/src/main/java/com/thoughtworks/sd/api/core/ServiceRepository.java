package com.thoughtworks.sd.api.core;

import java.util.Optional;

public interface ServiceRepository {
    Optional<Service> findByName(String name);
}
