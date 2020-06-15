package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Contrato;
import org.hibernate.Session;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.YearMonth;
import java.util.concurrent.Future;

@Stateless
public class AjustadorContratoBean {
	
	@PersistenceContext
	private EntityManager em;

	@Asynchronous
	public Future<Double> ajustaContratoPercentualAsync(Contrato contrato, double percentual) {
		Double novoSaldo = ajustaContratoPercentualDireto(contrato, percentual);
		return new AsyncResult<>(novoSaldo);
	}
	
	
	public Double ajustaContratoPercentualDireto(Contrato contrato, double percentual) {
		
		System.out.println("executando*****... " + Thread.currentThread());
		
		Double novoSaldo = contrato.getSaldo() * (1 + percentual);
		contrato.setSaldo(novoSaldo);
		contrato.setUltimaCorrecao(YearMonth.now());
		
		try {
			System.out.println("AjustadorContratoBean.ajustaContratoPercentualDireto() > SLEEP 5 ms");
			Thread.sleep(5);
		} catch (InterruptedException e) {}
		
//		em.merge(contrato); //versão antiga
		em.unwrap(Session.class).update(contrato);

		return novoSaldo;
	}
}
