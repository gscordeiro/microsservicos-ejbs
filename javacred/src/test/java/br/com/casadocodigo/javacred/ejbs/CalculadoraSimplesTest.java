package br.com.casadocodigo.javacred.ejbs;


import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@EnableWeld
public class CalculadoraSimplesTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(CalculadoraSimplesBean.class).build();

    @Inject
    CalculadoraSimplesBean calculadora;

    @Test
    public void deve_calcular_vinte_porcento() {
        Assertions.assertEquals(2_000.0, calculadora.calculaPercentual(10_000.0, 20.0/100), 0.0001);
    }

}
