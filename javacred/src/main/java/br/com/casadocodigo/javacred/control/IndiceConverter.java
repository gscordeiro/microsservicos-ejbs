package br.com.casadocodigo.javacred.control;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Indice;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(forClass = Indice.class, managed = true)
public class IndiceConverter implements Converter<Indice> {

	@Inject
	private ContratoBean contratoBean;

	@Override
	public Indice getAsObject(FacesContext fc, UIComponent comp, String string) {
		if(string == null || string.isEmpty()) return null;

		return contratoBean.buscarIndice(Integer.valueOf(string));
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent comp, Indice indice) {
		if(indice == null) return null;

		return String.valueOf(indice.getId());
	}
}
