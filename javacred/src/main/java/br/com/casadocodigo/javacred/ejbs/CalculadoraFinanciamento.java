package br.com.casadocodigo.javacred.ejbs;

import jakarta.ejb.Remove;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


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
