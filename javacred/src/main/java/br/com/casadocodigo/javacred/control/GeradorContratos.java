package br.com.casadocodigo.javacred.control;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.Indice;
import br.com.casadocodigo.javacred.entidades.IndiceValor;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Stateless
@Path("/gera-contratos")
public class GeradorContratos {

	public static final String NOME_INDICE = "IPCAE";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContratoBean contratoBean;

	/**
	 * Retorna o índice IPCAE, criando-o (com seus valores) apenas se ainda não existir.
	 * Compartilhado pelo seed (StartupConfig) e pela geração em massa, garantindo que
	 * todo contrato tenha um índice válido e evitando violar o unique de Indice.nome
	 * caso a geração rode mais de uma vez.
	 */
	public Indice recuperarOuCriarIndice(){
		List<Indice> existentes = em.createQuery(
						"select i from Indice i where i.nome = :nome", Indice.class)
				.setParameter("nome", NOME_INDICE)
				.getResultList();

		if(!existentes.isEmpty()){
			return existentes.get(0);
		}

		Indice indice = new Indice(NOME_INDICE);
		indice.setValores(Arrays.asList(
				new IndiceValor(indice, YearMonth.now().minusMonths(2), 0.01),
				new IndiceValor(indice, YearMonth.now().minusMonths(1), 0.02),
				new IndiceValor(indice, YearMonth.now(), 0.03)
		));
		em.persist(indice);
		return indice;
	}

	@GET
	public void geraContratos(){

		long inicio = System.currentTimeMillis();

		Indice indice = recuperarOuCriarIndice();


		for (int i = 0; i < 1_000; i++) {
			Contrato contrato = new Contrato();
			contrato.setValor(1_000_000.0);
			contrato.setSaldo(1_000_000.0);
			contrato.setIndiceCorrecao(indice);
			contrato.setUltimaCorrecao(YearMonth.now().minusMonths(2));
			em.persist(contrato);
		}
		
		long fim = System.currentTimeMillis();
		System.out.printf("Contratos gerados com sucesso. "
				+ "Tempo %d ms\n", fim-inicio);
	}
}
