package br.com.casadocodigo.javacred.restclient;


import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.security.KeyStore;

public abstract class JavacredTestBase {

    static WebTarget javacred;

    @BeforeClass
    public static void criaCliente() throws Exception {

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client;
        String caminhoServico;

        caminhoServico = "http://localhost:8080/javacred/rest";

        client = clientBuilder.build();

        javacred = client.target(caminhoServico);
    }

}
