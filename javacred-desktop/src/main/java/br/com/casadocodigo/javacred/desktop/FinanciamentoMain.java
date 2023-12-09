package br.com.casadocodigo.javacred.desktop;

import br.com.casadocodigo.javacred.ejbs.CalculadoraFinanciamentoRemota;

import jakarta.naming.Context;
import jakarta.naming.InitialContext;

public class FinanciamentoMain {

    public static void main(String[] args) throws Exception {

        Context context = new InitialContext();

        String nomeJndi = "ejb:/javacred/CalculadoraFinanciamento!" + CalculadoraFinanciamentoRemota.class.getName();

        CalculadoraFinanciamentoRemota calculadora
                = (CalculadoraFinanciamentoRemota) context.lookup(nomeJndi);

        double parcela = calculadora.simulaFinanciamento(30_000.0, 10);
        System.out.println(parcela);
    }
}
