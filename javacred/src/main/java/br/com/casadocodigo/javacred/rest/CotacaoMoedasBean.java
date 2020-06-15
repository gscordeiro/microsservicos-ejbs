package br.com.casadocodigo.javacred.rest;



import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Path("/cotacao")
@Produces(MediaType.APPLICATION_JSON)
public class CotacaoMoedasBean {

    @Resource
    private ManagedExecutorService mes;

    @GET
    @Path("/dolar/{quantidade}")
    public void cotarDolar(@PathParam("quantidade") Integer quantidade, @Suspended AsyncResponse response){

        cotar(Cotacao.Moeda.DOLAR, Cotacao.Moeda.REAL, quantidade, response);

    }

    @GET
    @Path("/euro/{quantidade}")
    public void cotarLibra(@PathParam("quantidade") Integer quantidade, @Suspended AsyncResponse response){

        cotar(Cotacao.Moeda.EURO, Cotacao.Moeda.DOLAR, quantidade, response);

    }

    private void cotar(Cotacao.Moeda cotar, Cotacao.Moeda destino, Integer quantidade, AsyncResponse response){
        String threadPrincipal = Thread.currentThread().getName();

        System.out.printf("Thread principal >> %s em %s :: %s \n", cotar, destino, threadPrincipal);

        mes.execute(() -> {
                    try {
                        String threadFilha = Thread.currentThread().getName();

                        System.out.printf("Thread filha >> %s em %s :: %s \n", cotar, destino, threadFilha);

                        TimeUnit.SECONDS.sleep(3);

                        Double valorUnitario = new Random().nextDouble() * 10;
                        Cotacao cotacao = new Cotacao(quantidade, valorUnitario * quantidade, cotar, destino);

                        System.out.printf("Thread filha << %s em %s :: %s \n", cotar, destino, threadFilha);

                        response.resume(Response.ok(cotacao).build());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        System.out.printf("Thread principal << %s em %s :: %s \n", cotar, destino, threadPrincipal);
    }

}
