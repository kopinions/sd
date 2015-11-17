package com.thoughtworks.sd.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.core.TemplateRepository;
import com.thoughtworks.sd.api.impl.MesosDns;
import com.thoughtworks.sd.api.impl.records.InMemTemplateRepository;
import com.thoughtworks.sd.api.impl.records.InMemoryServiceRepository;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.Injections;

import java.io.IOException;
import java.net.URI;

public class ApiModule {
    public void run() throws IOException {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("dns_entry_point")).to("http://192.168.50.4:8123");
                bind(ServiceRepository.class).to(InMemoryServiceRepository.class);
                bind(TemplateRepository.class).to(InMemTemplateRepository.class);
                bind(MesosDns.class);
            }
        });
        ServiceLocator locator = Injections.createLocator();
        WebappContext context = new WebappContext("Services API", "/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8081"), new SdResourceConfig(locator, injector), locator);
        context.deploy(server);
        server.start();
    }
}
