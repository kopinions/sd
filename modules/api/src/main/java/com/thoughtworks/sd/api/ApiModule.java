package com.thoughtworks.sd.api;

import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.impl.records.InMemoryServiceRepository;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.net.URI;

public class ApiModule {
    public ServiceRepository serviceRepository ;
    private MesosDriverHolder mesosDriverHolder;

    public ApiModule(MesosDriverHolder mesosDriverHolder) {

        this.mesosDriverHolder = mesosDriverHolder;
    }


    public void run() throws IOException {
        InMemoryServiceRepository inMemoryServiceRepository = new InMemoryServiceRepository();
        inMemoryServiceRepository.mesosDnsEntryPoint = "http://192.168.50.4:8123";
        serviceRepository = inMemoryServiceRepository;
        WebappContext context = new WebappContext("Services API", "/");

        ResourceConfig packages = new ResourceConfig()
                .packages("com.thoughtworks.sd")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind("http://192.168.50.4:8123").to(String.class).named("mesos-dns");
                        bind(serviceRepository).to(ServiceRepository.class);
                        bind(mesosDriverHolder).to(MesosDriverHolder.class);
                    }
                });

        ServletRegistration servletRegistration = context.addServlet("ServletContainer",
                new ServletContainer(packages));

        servletRegistration.addMapping("/*");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8081"));
        context.deploy(server);

        server.start();
    }
}
