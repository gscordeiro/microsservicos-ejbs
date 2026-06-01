package br.com.casadocodigo.javacred.rest;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * Lista os índices de correção disponíveis. Necessário para que os clientes REST
 * (curl / .http / Swagger) descubram o id a informar ao registrar um empréstimo,
 * papel que na tela JSF era do combo de índice.
 */
@Path("/indice")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Índices", description = "Índices de correção disponíveis")
public class IndiceResource {

    @Inject
    private ContratoBean contratoBean;

    @GET
    @Operation(summary = "Lista os índices de correção")
    public List<Indice> listar() {
        return contratoBean.buscarIndices();
    }
}
