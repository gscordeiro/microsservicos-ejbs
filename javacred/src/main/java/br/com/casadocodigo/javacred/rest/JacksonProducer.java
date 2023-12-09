package br.com.casadocodigo.javacred.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

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