package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class EmprestimoBean {

	private ContratoBean contratoBean;
	
	private ServicoAnaliseFinanceira saf;
        
	private EJBContext ejbContext;

	public EmprestimoBean(){
		System.out.println("EmprestimoBean.EmprestimoBean");
	}

	@Resource
	public void setEjbContext(EJBContext ejbContext) {
		System.out.println("ejbContext = [" + ejbContext + "]");
		this.ejbContext = ejbContext;
	}

	@Inject
	public void config(ContratoBean contratoBean, ServicoAnaliseFinanceira saf){
		System.out.println("contratoBean = [" + contratoBean + "], saf = [" + saf + "]");
		this.contratoBean = contratoBean;
		this.saf = saf;
	}

	public void registrarEmprestimo(Contrato contrato){
		
		contratoBean.salvar(contrato);
	
		try {
			saf.analisar(contrato.getCliente().getNome(), 
						 contrato.getValor());
			
		} 
		catch (JavacredApplicationException e) {
			
			if(contrato.getCliente().isPreferencial()){
				
				System.out.println("##### Como é um cliente preferencial, "
						+ "vou ignorar o SAF e manter o contrato!");
				
			}
			else {
				System.out.println("$$$$$$ Como NÃO é um cliente preferencial, "
						+ "vou observar o SAF e descartar o contrato!");
				ejbContext.setRollbackOnly();
				throw e;
			}
			
		} 

	}
	
	public void quitar(Contrato contrato) {
		
		contratoBean.quitar(contrato);
		
		saf.registrarQuitacao(contrato.getCliente().getNome(), contrato.getValor());
		
	}
	
	public List<Contrato> listarContratos(){
		return contratoBean.listarTodos();
	}

	
}
