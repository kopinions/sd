package com.thoughtworks.sd.api;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.core.TemplateRepository;
import com.thoughtworks.sd.api.impl.MesosDns;
import com.thoughtworks.sd.api.impl.records.InMemTemplateRepository;
import com.thoughtworks.sd.api.impl.records.InMemoryServiceRepository;

public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("dns_entry_point")).to("http://192.168.50.4:8123");
        bind(ServiceRepository.class).to(InMemoryServiceRepository.class);
        bind(TemplateRepository.class).to(InMemTemplateRepository.class);
        bind(MesosDns.class);
    }
}
