package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Correção de saldo assíncrona usando Jakarta Concurrency 3.1 (Jakarta EE 11):
 * {@code @Asynchronous} em bean CDI devolvendo {@link CompletableFuture}, no lugar
 * do antigo EJB {@code @Asynchronous} + {@code Future}/{@code AsyncResult}.
 *
 * A chamada a {@code contratoBean.corrigeContratos} passa pelo proxy do EJB
 * (atributo de transação REQUIRED), garantindo uma transação na thread gerenciada.
 */
@ApplicationScoped
public class CorrecaoAsyncService {

    @Inject
    private ContratoBean contratoBean;

    @Asynchronous
    public CompletableFuture<Double> corrigir(List<Indice> indices) {
        return Asynchronous.Result.complete(contratoBean.corrigeContratos(indices));
    }
}
