package br.com.casadocodigo.javacred.ejbs;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;

@ExtendWith(ArquillianExtension.class)
public class CalculadoraSimplesTest {

    @Deployment
    public static Archive createDeployment() {
        Archive jar = ShrinkWrap.create(JavaArchive.class)
                .addClass(CalculadoraSimplesBean.class);
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }



    @Inject
    CalculadoraSimplesBean calculadora;

    @Test
    public void deve_calcular_vinte_porcento() {
        Assertions.assertEquals(2_000.0, calculadora.calculaPercentual(10_000.0, 20.0/100), 0.0001);
    }

}
