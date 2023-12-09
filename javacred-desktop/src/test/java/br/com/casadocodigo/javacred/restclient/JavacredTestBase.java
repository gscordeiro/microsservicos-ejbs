package br.com.casadocodigo.javacred.restclient;


import org.junit.jupiter.api.BeforeAll;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import java.security.KeyStore;

public abstract class JavacredTestBase {

    public static final String JAVACRED_BASE_URI = "http://localhost:8080/javacred/rest";
    static WebTarget javacred;

    @BeforeAll
    public static void criaCliente() throws Exception {

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client;
        String caminhoServico;

        caminhoServico = "http://localhost:8080/javacred/rest";

        client = clientBuilder.build();

        javacred = client.target(caminhoServico);
    }

}
