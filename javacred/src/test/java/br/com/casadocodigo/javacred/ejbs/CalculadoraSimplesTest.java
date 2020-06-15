package br.com.casadocodigo.javacred.ejbs;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
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
        Assert.assertEquals(2_000.0, calculadora.calculaPercentual(10_000.0, 20.0/100), 0.0001);
    }

}
