package br.com.casadocodigo.javacred.control;

import br.com.casadocodigo.javacred.ejbs.ClienteBean;
import br.com.casadocodigo.javacred.entidades.Cliente;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

//forma de fazer com JSF 2.2
//@Named("clienteConverter")
//@RequestScoped
@FacesConverter(forClass = Cliente.class, managed = true)
public class ClienteConverter implements Converter<Cliente> {

	@Inject
	private ClienteBean clienteBean;
	
	@Override
	public Cliente getAsObject(FacesContext fc, UIComponent comp, String string) {
		if(string == null || string.isEmpty()) return null;
		
		return clienteBean.buscarPorId(Integer.valueOf(string));
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent comp, Cliente cliente) {
		if(cliente == null) return null;
		
		return String.valueOf(cliente.getId());
	}
}
