package br.com.casadocodigo.javacred.restclient;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class SimuladorFinanciamentoTest {

    @Test
    public void testaSimulacaoFinanciamento(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/"
                + "javacred/rest/financiamento/"
                + "simular?valor=12000&meses=10");
        Response response = target.request().get();
        Double resultado = response.readEntity(Double.class);
        response.close();

        Assert.assertEquals(1320.0, resultado, 0.001);
    }

    @Test
    public void testaSimulacaoFinanciamentoComParametros(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget javacred = client.target("http://localhost:8080/javacred/rest/");

        WebTarget simuladorFinanciamento = javacred.path("financiamento");

        Response response = simuladorFinanciamento.path("simular")
                .queryParam("valor", 12000.0)
                .queryParam("meses", 10)
                .request().get();

        Double resultado = response.readEntity(Double.class);
        response.close();

        Assert.assertEquals(1320.0, resultado, 0.001);
    }
}
