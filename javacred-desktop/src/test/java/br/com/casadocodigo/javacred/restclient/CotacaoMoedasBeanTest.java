package br.com.casadocodigo.javacred.restclient;

import br.com.casadocodigo.javacred.rest.Cotacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CotacaoMoedasBeanTest extends JavacredTestBase {



    @Test
    public void testaCotacaoDolar() throws ExecutionException, InterruptedException {

        WebTarget cotacaoMoedasBean = javacred.path("cotacao/dolar")
                .path("{quantidade}")
                .resolveTemplate("quantidade", 50);

        Future<Response> response = cotacaoMoedasBean
                .request(MediaType.APPLICATION_JSON)
                .async()
                .get();

        String json = response.get().readEntity(String.class);
        System.out.println(json);

    }

    @Test
    public void testaCotacaoDolarRx() throws ExecutionException, InterruptedException {

        WebTarget cotacaoMoedasBean = javacred.path("cotacao/dolar")
                .path("{quantidade}")
                .resolveTemplate("quantidade", 44);

        CompletionStage<Response> stage = cotacaoMoedasBean
                .request(MediaType.APPLICATION_JSON)
                .rx()
                .get();

        stage.thenApply(resp -> resp.readEntity(String.class)).thenAccept(System.out::println);

        Thread.sleep(5000);

    }


    @Test
    public void testaCotacaoEuroEmReal() throws ExecutionException, InterruptedException {

        WebTarget cotacaoMoedasBean = javacred.path("cotacao");

        Future<Response> response = cotacaoMoedasBean
                .path("euro/{quantidade}")
                .resolveTemplate("quantidade", 50)
                .request(MediaType.APPLICATION_JSON)
                .async()
                .get();

        Cotacao euroDolar = response.get().readEntity(Cotacao.class);
        System.out.println(euroDolar);

        response = cotacaoMoedasBean
                .path("dolar/{quantidade}")
                .resolveTemplate("quantidade", 50)
                .request(MediaType.APPLICATION_JSON)
                .async()
                .get();

        Cotacao dolarReal = response.get().readEntity(Cotacao.class);
        System.out.println(dolarReal);

        Double euroEmReal = euroDolar.getValor() * dolarReal.getValor();

        System.out.println(euroEmReal);

    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testaCotacaoEuroEmRealRx() throws ExecutionException, InterruptedException {

        WebTarget cotacaoMoedasBean = javacred.path("cotacao");

        CompletionStage<Double> euroDolarStage = cotacaoMoedasBean
                .path("euro/{quantidade}")
                .resolveTemplate("quantidade", 50)
                .request(MediaType.APPLICATION_JSON)
                .rx()
                .get()
                .thenApply(resp -> resp.readEntity(Cotacao.class).getValor());


        CompletionStage<Double> dolarRealStage = cotacaoMoedasBean
                .path("dolar/{quantidade}")
                .resolveTemplate("quantidade", 50)
                .request(MediaType.APPLICATION_JSON)
                .rx()
                .get()
                .thenApply(resp -> resp.readEntity(Cotacao.class).getValor());


        CompletionStage<Double> combinadoStage = euroDolarStage.thenCombine(dolarRealStage, (euroDolar, dolarReal) -> {

            Double euroEmReal = euroDolar * dolarReal;

            return euroEmReal;
        });


        combinadoStage.thenAccept(System.out::println);

        Thread.sleep(4000);

    }
}
