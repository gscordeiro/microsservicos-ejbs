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
public class ClienteResource {

    @Inject
    private ClienteBean clienteBean;

    @GET
    @Path("/todos")
    public List<Cliente> listarTodos() {
        return clienteBean.listarTodos();
    }

    @POST
    public Cliente salvar(Cliente cliente) {
        // mappedBy="cliente": o lado dono é Contrato, então amarramos a volta
        // antes de persistir para o cascade ALL gravar os contratos com a FK.
        for (Contrato contrato : cliente.getContratos()) {
            contrato.setCliente(cliente);
        }
        clienteBean.salvar(cliente);
        return cliente;
    }
}
