package com.thoughtworks.sd.api.impl.records;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.Template;

import java.util.Map;

public class TemplateRecord implements Template {
    private String id;
    private Map<String, Object> template;

    public TemplateRecord(String id, Map<String, Object> template) {
        this.id = id;
        this.template = template;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Service instantiation(Map<String, Object> params) {
        return null;
    }
}
