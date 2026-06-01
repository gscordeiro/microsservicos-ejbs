package br.com.casadocodigo.javacred.rest;

import br.com.casadocodigo.javacred.ejbs.ClienteBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * Substitui as telas clientes.xhtml / cliente.xhtml (ClienteController).
 * Demonstra injeção do EJB Stateful ClienteBean e o cascade PERSIST de
 * Cliente -> Contratos (antes feito pelo @ViewScoped + f:ajax).
 */
@Path("/cliente")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes", description = "Cadastro de clientes e seus contratos (cascade JPA)")
public class ClienteResource {

    @Inject
    private ClienteBean clienteBean;

    @GET
    @Path("/todos")
    @Operation(summary = "Lista todos os clientes")
    public List<Cliente> listarTodos() {
        return clienteBean.listarTodos();
    }

    @POST
    @Operation(summary = "Cadastra um cliente com seus contratos (todo contrato exige índice)")
    public Response salvar(Cliente cliente) {
        // mappedBy="cliente": o lado dono é Contrato, então amarramos a volta
        // antes de persistir para o cascade ALL gravar os contratos com a FK.
        // O índice de cada contrato vem no corpo (só o id) e é validado/resolvido
        // no ClienteBean.
        for (Contrato contrato : cliente.getContratos()) {
            contrato.setCliente(cliente);
        }

        try {
            clienteBean.salvar(cliente);
            return Response.ok(cliente).build();
        } catch (Exception e) {
            return Response.status(422) // Unprocessable Entity
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
