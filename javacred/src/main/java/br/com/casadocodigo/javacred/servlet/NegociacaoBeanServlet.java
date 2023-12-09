package br.com.casadocodigo.javacred.servlet;

import br.com.casadocodigo.javacred.ejbs.NegociacaoBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static br.com.casadocodigo.javacred.entidades.FormaPagamento.*;

@WebServlet("/negociacao-bean")
public class NegociacaoBeanServlet extends HttpServlet {

	@EJB
	private NegociacaoBean negociacao;

	protected void doGet(HttpServletRequest req,
						 HttpServletResponse resp)
			throws ServletException, IOException {

		Cliente cliente = buscarClientePorCPF(1234567890);

		negociacao.iniciaNegociacao(cliente);

		negociacao.registrarProposta(VISTA);
		negociacao.registrarProposta(ATE_6_VEZES);
		negociacao.registrarProposta(MAIS_6_VEZES);

		negociacao.getPropostas().forEach(System.out::println);

		resp.getWriter().printf("Teste finalizado");
	}

	private Cliente buscarClientePorCPF(int cpf) {
		Set<Contrato> contratos = new HashSet<>();

		Contrato contrato1 = new Contrato();
		contrato1.setValor(100_000.0);
		contrato1.setSaldo(40_000.0);
		contratos.add(contrato1);

		Contrato contrato2 = new Contrato();
		contrato2.setValor(100_000.0);
		contrato2.setSaldo(60_000.0);
		contratos.add(contrato2);

		Cliente cliente = new Cliente("Fulano", false);
		cliente.setContratos(contratos);

		return cliente;
	}

}
