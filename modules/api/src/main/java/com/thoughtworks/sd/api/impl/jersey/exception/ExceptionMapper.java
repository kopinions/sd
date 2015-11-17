package com.thoughtworks.sd.api.impl.jersey.exception;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<RuntimeException> {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Response toResponse(RuntimeException exception) {
        logger.error("exception:", exception);
        exception.printStackTrace();
        return Response.status(500).entity(exception.getMessage()).build();
    }
}
