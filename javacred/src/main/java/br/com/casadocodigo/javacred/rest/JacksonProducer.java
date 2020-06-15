package br.com.casadocodigo.javacred.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonProducer implements ContextResolver<ObjectMapper> {

    private final ObjectMapper json;


    public JacksonProducer() throws Exception {

        this.json = new ObjectMapper()
                .findAndRegisterModules();
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return json;
    }
}