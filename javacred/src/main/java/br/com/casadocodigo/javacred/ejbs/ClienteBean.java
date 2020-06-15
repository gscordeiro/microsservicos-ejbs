package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Cliente;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
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
