package br.com.casadocodigo.javacred.control;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.casadocodigo.javacred.ejbs.ClienteBean;
import br.com.casadocodigo.javacred.ejbs.EmprestimoBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;

@Named
@RequestScoped
public class EmprestimoController {

	
	private List<Cliente> clientes;
	
	private Contrato contrato;
	private List<Contrato> contratos;
	
	@Inject
	private ClienteBean clienteBean;
	
	@Inject
	private EmprestimoBean emprestimoBean;
	
	@PostConstruct
	public void init(){
		contrato = new Contrato();
		clientes = clienteBean.listarTodos(); //FIXME lazy
	}
	
	public List<Contrato> getContratos() {
		if(contratos == null){
			contratos = emprestimoBean.listarContratos();
		}
		return contratos;
	}
	
	public void contratar(){
		try {
			emprestimoBean.registrarEmprestimo(contrato);
			
		} catch (Exception e) {
			FacesContext fc  = FacesContext.getCurrentInstance();
			FacesMessage msg = new FacesMessage(e.getMessage());
			fc.addMessage(null, msg);
		}
	}
	
	
	public void quitar(Contrato contrato){
		
		try {
			
			emprestimoBean.quitar(contrato);
			
		} catch (Exception e) {
			FacesContext fc  = FacesContext.getCurrentInstance();
			FacesMessage msg = new FacesMessage(e.getMessage());
			fc.addMessage(null, msg);
			
			//limpa a lista para buscar de novo no banco e não parecer que a quitação deu certo.
			this.contratos = null;
		}
	}
	

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
}
