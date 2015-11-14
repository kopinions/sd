package com.thoughtworks.sd.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.net.URI;

public class ApiModule {
    public void run() throws IOException {
        WebappContext context = new WebappContext("Services API", "/");

        ServletRegistration servletRegistration = context.addServlet("ServletContainer",
                new ServletContainer(new ResourceConfig().packages("com.thoughtworks.sd")));

        servletRegistration.addMapping("/*");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8081"));
        context.deploy(server);

        server.start();
    }
}
