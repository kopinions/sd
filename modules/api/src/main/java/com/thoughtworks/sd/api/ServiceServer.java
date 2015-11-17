package com.thoughtworks.sd.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.Injections;

import java.io.IOException;
import java.net.URI;

public class ServiceServer {

    private HttpServer httpServer;
    private final WebappContext context;

    public ServiceServer(AbstractModule... modules) {
        ServiceLocator locator = Injections.createLocator();
        context = new WebappContext("Services API", "/");
        httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8081"), new SdResourceConfig(locator, Guice.createInjector(modules)), locator);
    }

    public void start() throws IOException {
        context.deploy(httpServer);
        httpServer.start();
    }


    public void stop() throws IOException {
        context.undeploy();
        httpServer.shutdownNow();
    }
}
