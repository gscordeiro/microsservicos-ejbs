package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Contrato;
//import org.hibernate.Session;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
		em.createQuery("update Contrato c set c.saldo = :saldo, c.ultimaCorrecao = :ultimaCorrecao where c.id = :id")
                        .setParameter("saldo", contrato.getSaldo())
                        .setParameter("ultimaCorrecao", contrato.getUltimaCorrecao())
                        .setParameter("id", contrato.getId())
                        .executeUpdate();

		return novoSaldo;
	}
}
