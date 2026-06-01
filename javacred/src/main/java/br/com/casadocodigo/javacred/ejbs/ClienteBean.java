package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.Indice;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import java.util.List;

@Named
@Stateful
@RequestScoped
public class ClienteBean {

	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager em;

	public List<Cliente> listarTodos(){
		System.out.println("ClienteBean.listarTodos");
		return em.createQuery("select c from Cliente c", Cliente.class).getResultList();
	}

	public Cliente buscarPorId(Integer id){
		return em.find(Cliente.class, id);
	}
	public void salvar(Cliente cliente){

		// Como num financiamento real, todo contrato precisa de um índice de
		// correção desde a contratação — mesma regra do registro de empréstimo.
		for (Contrato contrato : cliente.getContratos()) {
			Indice indice = contrato.getIndiceCorrecao();
			if (indice == null || indice.getId() == null) {
				throw new JavacredApplicationException("Todo contrato do cliente deve ter um índice de correção.");
			}
			// Re-attacha no MESMO contexto de persistência para o cascade PERSIST
			// não receber uma entidade destacada.
			Indice gerenciado = em.find(Indice.class, indice.getId());
			if (gerenciado == null) {
				throw new JavacredApplicationException("Índice de correção inexistente: " + indice.getId());
			}
			contrato.setIndiceCorrecao(gerenciado);
		}

		em.persist(cliente);
	}
	
	@Remove
	public void remover(){
		System.out.println("EJB marcado para morrer");
	}
	
	@PostConstruct
	public void init(){
		System.out.println("ClienteBean criado!!");
	}
	
	@PreDestroy
	public void removendo(){
		System.out.println("Fique tranquilo, sou um EJB Stateful "
				+ "mas já estou sendo removido da memória");
	}
}
