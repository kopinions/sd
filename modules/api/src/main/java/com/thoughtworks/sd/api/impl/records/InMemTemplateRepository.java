package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Template;
import com.thoughtworks.sd.api.core.TemplateRepository;
import com.thoughtworks.sd.api.impl.records.TemplateRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemTemplateRepository implements TemplateRepository {
    Map<String, Template> templates = new HashMap<>();

    @Override
    public Template create(Map<String, Object> request) {
        String key = UUID.randomUUID().toString();
        return templates.put(key, new TemplateRecord(key, request));
    }

    @Override
    public Optional<Template> findById(String id) {
        return Optional.ofNullable(templates.get(id));
    }
}
