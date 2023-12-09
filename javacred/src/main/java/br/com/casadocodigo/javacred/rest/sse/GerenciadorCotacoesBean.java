package br.com.casadocodigo.javacred.rest.sse;

import br.com.casadocodigo.javacred.rest.Cotacao;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Singleton
@Path("/acompanhar-cotacao")
public class GerenciadorCotacoesBean {

    @Context
    private Sse sse;

    private Map<Cotacao.Moeda, SseBroadcaster> broadcasters = new HashMap<>();

    @GET
    @Path("/dolar")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void acompanharCotacaoDolar(@Context SseEventSink eventSink){
        registrar(eventSink, Cotacao.Moeda.DOLAR);
    }

    @GET
    @Path("/euro")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void acompanharCotacaoEuro(@Context SseEventSink eventSink){
        registrar(eventSink, Cotacao.Moeda.EURO);
    }

    private void registrarJava7(SseEventSink eventSink, Cotacao.Moeda moedaAlvo){
        SseBroadcaster sseBroadcaster = broadcasters.get(moedaAlvo);
        if(sseBroadcaster == null){
            sseBroadcaster = sse.newBroadcaster();
            broadcasters.put(moedaAlvo, sseBroadcaster);
        }
        sseBroadcaster.register(eventSink);
    }

    private void registrar(SseEventSink eventSink, Cotacao.Moeda moedaAlvo){

        broadcasters
                .computeIfAbsent(moedaAlvo, moeda -> sse.newBroadcaster())
                .register(eventSink);
    }

    private Cotacao geraCotacao(){
        Random r = new Random();
        Cotacao.Moeda[] moedas = Cotacao.Moeda.values();
        Cotacao.Moeda cotada = moedas[r.nextInt(moedas.length)];
        return new Cotacao(1, r.nextDouble() * 10, cotada, Cotacao.Moeda.REAL);
    }

    //a cada 5 segundos
    @Schedule(hour = "*", minute = "*", second = "*/5")
    public void geradorCotacoes(){

        Cotacao cotacao = geraCotacao();
        cotacao.setMoedaCotada(Cotacao.Moeda.DOLAR);

        SseBroadcaster sseBroadcaster = broadcasters.get(cotacao.getMoedaCotada());

        if(sseBroadcaster != null){
            OutboundSseEvent event = sse.newEventBuilder()
                    .data(cotacao)
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .build();
            sseBroadcaster.broadcast(event);
        }
    }
}
