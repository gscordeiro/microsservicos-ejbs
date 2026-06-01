package br.com.casadocodigo.javacred.rest;

import br.com.casadocodigo.javacred.control.Benchmark;
import br.com.casadocodigo.javacred.control.GeradorContratos;
import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.ejbs.CorrecaoAsyncService;
import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * Substitui ajustaContratos.xhtml (AtualizadorSaldoController) para as estratégias
 * SÍNCRONAS de correção de saldo. Cada endpoint devolve um Benchmark em JSON
 * (duração + saldo geral) — o equivalente REST aos FacesMessages.
 *
 * As estratégias ASSÍNCRONAS (antes Future + p:poll) migram na Fase 3 para
 * CompletableFuture (Jakarta Concurrency 3.1) + SSE.
 */
@Path("/correcao")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Correção de saldo", description = "Benchmark de estratégias de concorrência para corrigir o saldo dos contratos")
public class CorrecaoSaldoResource {

    @Inject
    private ContratoBean contratoBean;

    @Inject
    private GeradorContratos geradorContratos;

    @Inject
    private CorrecaoAsyncService correcaoAsync;

    @GET
    @Path("/total")
    @Operation(summary = "Quantidade de contratos no banco")
    public long total() {
        return contratoBean.contaContratos();
    }

    @POST
    @Path("/gerar-contratos")
    @Operation(summary = "Gera 1000 contratos (todos com índice) para o benchmark")
    public long gerarContratos() {
        geradorContratos.geraContratos();
        return contratoBean.contaContratos();
    }

    @POST
    @Path("/sync")
    @Operation(summary = "Correção síncrona (sequencial)")
    public Benchmark sincrono() {
        return executar("síncrono", contratoBean.buscarIndices(), contratoBean::corrigeContratos);
    }

    @POST
    @Path("/paralelo")
    @Operation(summary = "Correção paralela (EJB @Asynchronous + Future)")
    public Benchmark paralelo() {
        return executar("paralelo", contratoBean.buscarIndices(), contratoBean::corrigeContratosPar);
    }

    @POST
    @Path("/mes")
    @Operation(summary = "Correção via ManagedExecutorService.invokeAll")
    public Benchmark viaManagedExecutorService() {
        return executar("MES", contratoBean.buscarIndices(), contratoBean::corrigeContratosViaMES2);
    }

    @GET
    @Path("/async-stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Operation(summary = "Correção assíncrona com resultado empurrado via SSE (Jakarta Concurrency 3.1 + CompletableFuture)")
    public void correcaoAsyncStream(@Context SseEventSink sink, @Context Sse sse) {
        long total = contratoBean.contaContratos();
        Benchmark benchmark = new Benchmark(total, "assíncrono (SSE)");

        // Empurra o "início" imediatamente; o resultado chega quando o
        // CompletableFuture completar — sem polling (substitui o p:poll do JSF).
        sink.send(sse.newEventBuilder()
                .name("inicio")
                .data(String.format("Processando %d contratos de forma assíncrona...", total))
                .build());

        correcaoAsync.corrigir(contratoBean.buscarIndices())
                .thenAccept(saldo -> {
                    benchmark.setResultado(saldo);
                    benchmark.stop();
                    sink.send(sse.newEventBuilder()
                                    .name("resultado")
                                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                                    .data(Benchmark.class, benchmark)
                                    .build())
                            .whenComplete((r, e) -> fechar(sink));
                })
                .exceptionally(erro -> {
                    sink.send(sse.newEventBuilder().name("erro").data(String.valueOf(erro.getMessage())).build())
                            .whenComplete((r, e) -> fechar(sink));
                    return null;
                });
    }

    private static void fechar(SseEventSink sink) {
        try {
            sink.close();
        } catch (Exception ignorado) {
            // conexão encerrando; nada a fazer
        }
    }

    private Benchmark executar(String nome, List<Indice> indices, java.util.function.ToDoubleFunction<List<Indice>> estrategia) {
        Benchmark benchmark = new Benchmark(contratoBean.contaContratos(), nome);
        benchmark.setResultado(estrategia.applyAsDouble(indices));
        benchmark.stop();
        return benchmark;
    }
}
