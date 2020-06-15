package br.com.casadocodigo.javacred.control;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.Indice;
import br.com.casadocodigo.javacred.entidades.IndiceValor;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.YearMonth;
import java.util.Arrays;

@Stateless
@Path("/gera-contratos")
public class GeradorContratos {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContratoBean contratoBean;
	
	@GET
	public void geraContratos(){
		
		long inicio = System.currentTimeMillis();

		Indice igpm = new Indice("IPCAE");
		igpm.setValores(Arrays.asList(
				new IndiceValor(igpm, YearMonth.now().minusMonths(2), 0.01),
				new IndiceValor(igpm, YearMonth.now().minusMonths(1), 0.02),
				new IndiceValor(igpm, YearMonth.now(), 0.03)
		));


		em.persist(igpm);

		
		for (int i = 0; i < 1_000; i++) {
			Contrato contrato = new Contrato();
			contrato.setValor(1_000_000.0);
			contrato.setSaldo(1_000_000.0);
			contrato.setIndiceCorrecao(igpm);
			contrato.setUltimaCorrecao(YearMonth.now().minusMonths(2));
			em.persist(contrato);
		}
		
		long fim = System.currentTimeMillis();
		System.out.printf("Contratos gerados com sucesso. "
				+ "Tempo %d ms\n", fim-inicio);
	}
}
