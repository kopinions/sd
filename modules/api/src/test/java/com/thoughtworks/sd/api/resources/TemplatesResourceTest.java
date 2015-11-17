package com.thoughtworks.sd.api.resources;

import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.core.Template;
import com.thoughtworks.sd.api.core.TemplateRepository;
import com.thoughtworks.sd.api.impl.records.InMemoryServiceRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.glassfish.grizzly.http.util.HttpStatus.CREATED_201;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TemplatesResourceTest extends JerseyTest {

    private Response createTemplateResponse;
    HashMap<Object, Object> serviceData = new HashMap<>();

    ServiceRepository serviceRepository = new InMemoryServiceRepository();
    TemplateRepository templateRepository = Mockito.mock(TemplateRepository.class);

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig()
                .packages("com.thoughtworks.sd")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(serviceRepository).to(ServiceRepository.class);
                        bind(templateRepository).to(TemplateRepository.class);
                    }
                });
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(templateRepository.create(anyMap()))
                .thenReturn(
                        new Template() {
                            @Override
                            public String getId() {
                                return "stub";
                            }

                            @Override
                            public Service instantiation(Map<String, Object> params) {
                                return Mockito.mock(Service.class);
                            }
                        }
                );
        createTemplateResponse = target("/templates")
                .request()
                .post(Entity.json(serviceData));
    }

    @Test
    public void should_able_to_create_service() throws Exception {
        assertThat(createTemplateResponse.getStatus(), is(CREATED_201.getStatusCode()));
    }
}