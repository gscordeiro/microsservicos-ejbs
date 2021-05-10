package br.com.casadocodigo.javacred.restclient;

import br.com.casadocodigo.javacred.rest.Cotacao;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;


public class CotacaoMoedasSSETest extends JavacredTestBase {

    @Test
    public void testeSimplesSSE(){

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/javacred/rest/acompanhar-cotacao/dolar");

        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(System.out::println);
            source.open();
            Thread.sleep(20000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void testaAcompanhamentoEventos() throws InterruptedException {

        SseEventSource eventSource = SseEventSource.target(javacred.path("acompanhar-cotacao/dolar")).build();

        System.out.println("Teste executado na thread: " + Thread.currentThread().getName());

        eventSource.register(this::processaEvento, this::processaExcecao, this::execucaoFinalizada);

        eventSource.open();
        Thread.sleep(20000);
        eventSource.close();
    }

    public void processaEvento(InboundSseEvent sseEvent){
        System.out.println("Evento recebido na thread: " + Thread.currentThread().getName());
        System.out.println("Dado JSON: " + sseEvent.readData());
        System.out.println("JSON em Objeto: " + sseEvent.readData(Cotacao.class, MediaType.APPLICATION_JSON_TYPE));
    }

    public void processaExcecao(Throwable t){
        System.err.println("Erro: " + t);
    }

    public void execucaoFinalizada(){
        System.out.println("Execucao finalizada.");
    }

}
