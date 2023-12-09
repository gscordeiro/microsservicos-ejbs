package br.com.casadocodigo.javacred.servlet;

import java.io.IOException;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import br.com.casadocodigo.javacred.ejbs.CalculadoraFinanciamentoRemota;

@WebServlet("/calculadora-financiamento")
public class CalculadoraFinanciamentoServlet extends HttpServlet{

	private static final long serialVersionUID = -3073141701900181410L;
	
	@EJB
	private CalculadoraFinanciamentoRemota calculadora;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		double parcela = calculadora.simulaFinanciamento(40000.0, 10);
		System.out.printf("\nvalor da parcela %.2f", parcela);
		resp.getWriter().printf("valor da parcela %.2f", parcela);

	}
}
