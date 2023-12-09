package br.com.casadocodigo.javacred.control;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import br.com.casadocodigo.javacred.ejbs.ClienteBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;

@Named
@ViewScoped
public class ClienteController implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private ClienteBean clienteBean;
	
	private List<Cliente> clientes;

	private Cliente cliente = new Cliente();
	
	@PostConstruct
	public void init(){
		System.out.println("ClienteController.init()");
	}
	
	public List<Cliente> getClientes() {
		if (clientes == null) {
			clientes = clienteBean.listarTodos();
		}
		return clientes;
	}
	
	public void adicionarContrato(){
		cliente.getContratos().add(new Contrato(cliente));
	}
	
	public String salvar(){
		clienteBean.salvar(cliente);
		return "clientes";
	}

	public Cliente getCliente() {
		return cliente;
	}

	@PreDestroy
	public void antesDaRemocao(){
		System.out.println("Antes de morrer, vou matar o EJB");
//		clienteBean.remover();
	}
}
