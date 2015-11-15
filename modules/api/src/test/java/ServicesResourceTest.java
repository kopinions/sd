import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;
import com.thoughtworks.sd.api.impl.records.ServiceRecord;
import junit.framework.TestCase;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ServicesResourceTest extends JerseyTest {

    private Response createServiceResult;
    HashMap<Object, Object> serviceData = new HashMap<>();


    @Mock
    ServiceRepository serviceRepository;

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
                    }
                });
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(serviceRepository.findByName(eq("mysql"))).thenReturn(Optional.of(new ServiceRecord()));
        serviceData.put("name", "mysql");
        serviceData.put("uri", "");
        createServiceResult = target("/services")
                .request()
                .post(Entity.json(serviceData));
    }

    @Test
    public void should_able_to_create_service() throws Exception {
        assertThat(createServiceResult.getStatus(), is(HttpStatus.CREATED_201.getStatusCode()));
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
}