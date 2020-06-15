package br.com.casadocodigo.javacred.ejbs;

import javax.ejb.Remote;

@Remote
public interface CalculadoraFinanciamentoRemota {

	double simulaFinanciamento(double valorEmprestimo, int meses);

}