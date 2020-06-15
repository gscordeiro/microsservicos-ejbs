package br.com.casadocodigo.javacred.ejbs;

import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Stateless
@Path("/financiamento")
@Produces(MediaType.APPLICATION_JSON)
public class CalculadoraFinanciamento implements CalculadoraFinanciamentoRemota{
	
	@GET @Path("/simular")
	public double simulaFinanciamento(@QueryParam("valor") double valorEmprestimo, @QueryParam("meses") int meses){
		
		double taxaJuros = 0.01; // 1% ao mês
		double totalJuros = meses * taxaJuros; // juros simples
		double valorDivida = valorEmprestimo * (1 + totalJuros);
		return valorDivida / meses;
	}
	
	@Remove
	public void remover(){
		System.out.println("CalculadoraFinanciamento.remover()");
	}
	

}
