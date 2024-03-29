package com.thoughtworks.sd.api.impl.jersey;


import com.thoughtworks.sd.api.impl.Record;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.thoughtworks.sd.api.impl.util.Json.toJson;


@Provider
public class RecordWriter implements MessageBodyWriter<Record> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Record.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Record record, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(Record record, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(entityStream)) {
            outputStreamWriter.write(toJson(record.toJson()));
        }
    }
}
