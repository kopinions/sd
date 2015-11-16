package com.thoughtworks.sd.api.resources;


import com.thoughtworks.sd.api.core.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("templates")
public class TemplatesResource {
    @POST
    @Path("{tid}/instances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response services(@PathParam("tid") String id,
                             @Context TemplateRepository templates) {
        return templates.findById(id)
                .map(s -> created(Routing.service(s.instantiation())).build())
                .orElse(status(NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Map<String, Object> request,
                           @Context TemplateRepository templates) {
        return created(URI.create(templates.create(request).getId())).build();
    }
}
