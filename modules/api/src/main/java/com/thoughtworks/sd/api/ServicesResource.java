package com.thoughtworks.sd.api;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("services")
public class ServicesResource {
    @GET
    public Response captcha() {
        return Response.ok().build();
    }
}
