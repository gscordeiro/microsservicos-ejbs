package br.com.casadocodigo.javacred.restclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class SimuladorFinanciamentoTest {

    @Test
    public void testaSimulacaoFinanciamento(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(JavacredTestBase.JAVACRED_BASE_URI + "/financiamento/"
                + "simular?valor=12000&meses=10");
        Response response = target.request().get();
        Double resultado = response.readEntity(Double.class);
        response.close();

        Assertions.assertEquals(1320.0, resultado, 0.001);
    }

    @Test
    public void testaSimulacaoFinanciamentoComParametros(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget javacred = client.target(JavacredTestBase.JAVACRED_BASE_URI + "/");

        WebTarget simuladorFinanciamento = javacred.path("financiamento");

        Response response = simuladorFinanciamento.path("simular")
                .queryParam("valor", 12000.0)
                .queryParam("meses", 10)
                .request().get();

        Double resultado = response.readEntity(Double.class);
        response.close();

        Assertions.assertEquals(1320.0, resultado, 0.001);
    }
}
