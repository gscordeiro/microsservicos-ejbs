package br.com.casadocodigo.javacred.ejbs;

import jakarta.ejb.Stateless;


@Stateless
public class CalculadoraSimplesBean {

    public double calculaPercentual(double valor, double percentual) {
        return valor * percentual;
    }
}