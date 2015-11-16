package com.thoughtworks.sd.api.core;

import java.util.Map;
import java.util.Optional;

public interface TemplateRepository {
    Template create(Map<String, Object> request);

    Optional<Template> findById(String id);
}
