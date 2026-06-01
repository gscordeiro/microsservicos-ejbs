package br.com.casadocodigo.javacred.rest;

import br.com.casadocodigo.javacred.ejbs.ClienteBean;
import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.ejbs.EmprestimoBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * Substitui as telas emprestimo.xhtml / emprestimos.xhtml (EmprestimoController).
 * Passa pelo EJB EmprestimoBean para preservar a demonstração do ServicoAnaliseFinanceira
 * (análise de crédito + rollback transacional no registro, e registro de quitação).
 */
@Path("/emprestimo")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Empréstimos", description = "Registro e quitação de empréstimos (EJB + análise de crédito)")
public class EmprestimoResource {

    @Inject
    private EmprestimoBean emprestimoBean;

    @Inject
    private ContratoBean contratoBean;

    @Inject
    private ClienteBean clienteBean;

    @GET
    @Path("/contratos")
    @Operation(summary = "Lista os contratos/empréstimos")
    public List<Contrato> listarContratos() {
        return emprestimoBean.listarContratos();
    }

    @POST
    @Operation(summary = "Registra um empréstimo (cliente e índice de correção obrigatórios)")
    public Response registrar(Contrato contrato) {
        // O JSON do cliente chega apenas com o id (como o selectOneMenu + ClienteConverter
        // faziam): resolvemos a entidade completa antes de registrar.
        Cliente cliente = contrato.getCliente();
        if (cliente != null && cliente.getId() != null) {
            contrato.setCliente(clienteBean.buscarPorId(cliente.getId()));
        }

        // Mesmo padrão do cliente: o corpo informa o índice só pelo id; resolvemos
        // a entidade completa. A obrigatoriedade é validada no EmprestimoBean.
        Indice indice = contrato.getIndiceCorrecao();
        if (indice != null && indice.getId() != null) {
            contrato.setIndiceCorrecao(contratoBean.buscarIndice(indice.getId()));
        }

        try {
            Contrato salvo = emprestimoBean.registrarEmprestimo(contrato);
            return Response.ok(salvo).build();
        } catch (Exception e) {
            // Espelha o FacesMessage do controller: devolve a mensagem de negócio.
            return Response.status(422) // Unprocessable Entity

                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @POST
    @Path("/{id}/quitar")
    @Operation(summary = "Quita um contrato (saldo vai a zero; registra a quitação)")
    public Response quitar(@PathParam("id") Integer id) {
        Contrato contrato = contratoBean.buscarContrato(id);
        if (contrato == null) {
            throw new NotFoundException("Contrato " + id + " não encontrado");
        }

        try {
            emprestimoBean.quitar(contrato);
            return Response.ok(contrato).build();
        } catch (Exception e) {
            return Response.status(422) // Unprocessable Entity

                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
