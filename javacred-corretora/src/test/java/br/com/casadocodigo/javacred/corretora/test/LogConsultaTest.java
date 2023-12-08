package br.com.casadocodigo.javacred.corretora.test;

import br.com.casadocodigo.javacred.corretora.entity.LogConsulta;
import br.com.casadocodigo.javacred.corretora.cliente.Consulta;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCacheFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Disabled
public class LogConsultaTest {

    @Test
    public void testaCache() throws ExecutionException, InterruptedException {
        Client client = ClientBuilder.newClient();
        client.register(new BrowserCacheFeature());

        WebTarget javacred = client.target("http://localhost:8080");

        WebTarget cotacaoBean = javacred.path("cotacao/v2");
        Response response = null;
        Double cotacaoCache = null;
//        Duration tempoCache = Duration.ofSeconds(10);
        Duration tempoCache = Duration.ofMinutes(15);
        Instant inicio = Instant.now();

        for(int i = 0; i < 6; i++){
            response = cotacaoBean.path("{codigo}").resolveTemplate("codigo", "BBAS3")
                    .queryParam("quantidade", 200)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

//            String json = response.readEntity(String.class);
            LogConsulta logConsulta = response.readEntity(LogConsulta.class);

            Duration tempoDecorrido = Duration.between(inicio, Instant.now());
            if(tempoDecorrido.compareTo(tempoCache) > 0){
                System.out.println("cache expirado!");
                inicio = Instant.now();
                cotacaoCache = null;
            }

            if (cotacaoCache == null){
                cotacaoCache = logConsulta.getValorAtivo();
            }

            Assertions.assertEquals(cotacaoCache, logConsulta.getValorAtivo(), 0.001);
            System.out.println(cotacaoCache + " = " + logConsulta.getValorAtivo());

            System.out.println(new Date());
            System.out.println(response.getStatus());
            System.out.println(logConsulta);
            System.out.println(response.getHeaders());

            System.out.println("=========SLEEP 5=========");
            Thread.sleep(5_000);
        }

        response.close();
    }

    @Test
    public void testaForm() {
        Form form = new Form();
        form.param("codigo", "PETR4")
        .param("quantidade", "200");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/cotacao/v2");
        Response response = target.
                request(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.form(form));

        LogConsulta logConsulta = response.readEntity(LogConsulta.class);
        System.out.println(response.getStatus());
        System.out.println(logConsulta);
    }

    @Test
    public void testaJson() throws ExecutionException, InterruptedException {
        Consulta consulta = new Consulta("PETR4", 100.0);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/cotacao/v2");
        Future<LogConsulta> response = target.
                request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.json(consulta)).submit(LogConsulta.class);

        LogConsulta logConsulta = response.get();
        System.out.println(logConsulta);
    }
}
