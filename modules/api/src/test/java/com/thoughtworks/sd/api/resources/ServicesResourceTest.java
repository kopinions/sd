package com.thoughtworks.sd.api.resources;

import com.google.inject.Scopes;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.impl.MesosDns;
import com.thoughtworks.sd.api.impl.records.InMemoryServiceRepository;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.glassfish.grizzly.http.util.HttpStatus.CREATED_201;
import static org.glassfish.grizzly.http.util.HttpStatus.NOT_FOUND_404;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ServicesResourceTest extends JerseyTest {

    private Response createServiceResult;
    HashMap<Object, Object> serviceData = new HashMap<>();

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig()
                .packages("com.thoughtworks.sd")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        MesosDns mock = Mockito.mock(MesosDns.class);
                        when(mock.getDnsInfo(any())).thenReturn(new HashMap<String, Object>(){{
                            put("service", "mysql");
                            put("host", "host");
                            put("ip", "127.0.0.1");
                            put("port", 1000);
                        }});

                        bind(mock).to(MesosDns.class);
                        bind(InMemoryServiceRepository.class).to(ServiceRepository.class).in(Singleton.class);
                    }
                });
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        serviceData.put("name", "mysql");
        serviceData.put("uri", "");
        createServiceResult = target("/services")
                .request()
                .post(Entity.json(serviceData));
    }

    @Test
    public void should_able_to_create_service() throws Exception {
        assertThat(createServiceResult.getStatus(), is(CREATED_201.getStatusCode()));
    }

    @Test
    public void should_able_to_get_service_data() throws Exception {
        Response existedService = target("/services")
                .path("mysql")
                .request()
                .get();
        assertThat(existedService.getStatus(), is(HttpStatus.OK_200.getStatusCode()));

        Map<String, Object> serviceData = existedService.readEntity(Map.class);
        assertThat(serviceData.get("name"), is("mysql"));
    }

    @Test
    public void should_able_to_not_found_when_service_not_found() throws Exception {
        Response existedService = target("/services")
                .path("not_exists")
                .request()
                .get();
        assertThat(existedService.getStatus(), is(NOT_FOUND_404.getStatusCode()));
    }


    @Test
    public void should_able_to_destroy_the_service() throws Exception {
        Response existedService = target("/services")
                .path("mysql")
                .request()
                .delete();
        assertThat(existedService.getStatus(), is(HttpStatus.OK_200.getStatusCode()));
    }
}