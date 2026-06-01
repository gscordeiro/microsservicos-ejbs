package br.com.casadocodigo.javacred.rest;

import br.com.casadocodigo.javacred.control.Benchmark;
import br.com.casadocodigo.javacred.control.GeradorContratos;
import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
public class CorrecaoSaldoResource {

    @Inject
    private ContratoBean contratoBean;

    @Inject
    private GeradorContratos geradorContratos;

    @GET
    @Path("/total")
    public long total() {
        return contratoBean.contaContratos();
    }

    @POST
    @Path("/gerar-contratos")
    public long gerarContratos() {
        geradorContratos.geraContratos();
        return contratoBean.contaContratos();
    }

    @POST
    @Path("/sync")
    public Benchmark sincrono() {
        return executar("síncrono", contratoBean.buscarIndices(), contratoBean::corrigeContratos);
    }

    @POST
    @Path("/paralelo")
    public Benchmark paralelo() {
        return executar("paralelo", contratoBean.buscarIndices(), contratoBean::corrigeContratosPar);
    }

    @POST
    @Path("/mes")
    public Benchmark viaManagedExecutorService() {
        return executar("MES", contratoBean.buscarIndices(), contratoBean::corrigeContratosViaMES2);
    }

    private Benchmark executar(String nome, List<Indice> indices, java.util.function.ToDoubleFunction<List<Indice>> estrategia) {
        Benchmark benchmark = new Benchmark(contratoBean.contaContratos(), nome);
        benchmark.setResultado(estrategia.applyAsDouble(indices));
        benchmark.stop();
        return benchmark;
    }
}
