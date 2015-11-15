package com.thoughtworks.sd.api.resources;


import com.thoughtworks.sd.api.core.Service;
import com.thoughtworks.sd.api.core.ServiceRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("services")
public class ServicesResource {
    @GET
    public Response services(@QueryParam("name") String name) {
        return ok().build();
    }

    @GET
    @Path("{sn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response services(@PathParam("sn") String name,
                             @Context ServiceRepository serviceRepository) {
        Optional<Service> service = serviceRepository.findByName(name);
        return service
                .map(s -> ok(s).build())
                .orElse(status(NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Map<String, Object> request,
                           @Context ServiceRepository services) {
        return Response.created(URI.create(services.create(request).getName())).build();
    }

}
