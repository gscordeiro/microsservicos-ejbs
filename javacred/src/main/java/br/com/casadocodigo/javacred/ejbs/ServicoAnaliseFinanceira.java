package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;
import br.com.casadocodigo.javacred.exceptions.JavacredException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import br.com.casadocodigo.javacred.entidades.RegistroEmprestimo;
import br.com.casadocodigo.javacred.entidades.RegistroEmprestimo.Tipo;

@Stateless
public class ServicoAnaliseFinanceira {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void analisar(String tomador, Double valor){
		
		List<RegistroEmprestimo> emprestimosDeHoje = buscarEmprestimos(tomador, new Date());
		
		if(!emprestimosDeHoje.isEmpty()){
			
			throw new JavacredApplicationException("Tomador já solictou empréstimo hoje!");
			
		}
		
		RegistroEmprestimo registro = new RegistroEmprestimo(tomador, valor, Tipo.CONTRATACAO);
		entityManager.persist(registro);
		
	}
	
	public void registrarQuitacao(String tomador, Double valor){
		
		if(new Random().nextBoolean()){
			throw new JavacredException("Sistema de Análise Financeira temporariamente indisponível.");
		}
		
		RegistroEmprestimo registro = new RegistroEmprestimo(tomador, valor, Tipo.QUITACAO);
		entityManager.persist(registro);
	}
	
	public List<RegistroEmprestimo> buscarEmprestimos(String tomador, Date data){
		String query = "select e from RegistroEmprestimo e "
				+ "where e.tomador = :tomador and data = :data";
		
		return entityManager.createQuery(query, RegistroEmprestimo.class)
				.setParameter("tomador", tomador)
				.setParameter("data", data)
				.getResultList();
	}
}
