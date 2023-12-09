package br.com.casadocodigo.javacred.ejbs;

import jakarta.ejb.Remote;

@Remote
public interface CalculadoraFinanciamentoRemota {

	double simulaFinanciamento(double valorEmprestimo, int meses);

}