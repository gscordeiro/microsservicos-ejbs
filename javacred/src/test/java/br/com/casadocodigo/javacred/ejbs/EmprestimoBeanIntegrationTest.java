package br.com.casadocodigo.javacred.ejbs;


import br.com.casadocodigo.javacred.entidades.*;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;
import br.com.casadocodigo.javacred.exceptions.JavacredException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import static org.mockito.Mockito.*;


@RunWith(Arquillian.class)
public class EmprestimoBeanIntegrationTest {


    @Deployment
    public static Archive createDeployment() {
        Archive archive = ShrinkWrap.create(WebArchive.class)
                .addClasses(EmprestimoBean.class,
                        ContratoBean.class,
                        AjustadorContratoBean.class,
                        ServicoAnaliseFinanceira.class,
                        JavacredApplicationException.class, JavacredException.class,
                        Contrato.class, Cliente.class, Indice.class, IndiceValor.class, RegistroEmprestimo.class)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries( //adiciona dependências via Maven
                        Maven.resolver().resolve("org.mockito:mockito-core:3.3.3").withTransitivity().asFile()
//                        Maven.resolver().resolve("org.mockito:mockito-all:1.10.19").withTransitivity().asFile()
                );

        System.out.println(archive.toString(true));
        return archive;
    }


    @Inject
    ContratoBean contratoBean;

    @Inject
    ServicoAnaliseFinanceira servicoAnaliseFinanceira;

    @Inject
    EmprestimoBean emprestimoBean;

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction utx;

    @Before
    public void limparDados() throws Exception {
        utx.begin();
        em.createQuery("delete from Contrato ").executeUpdate();
        em.createQuery("delete from RegistroEmprestimo").executeUpdate();
        utx.commit();
    }

    @Test
    public void cliente_nao_preferencial_deve_ter_segundo_emprestimo_rejeitado() {

        Assert.assertEquals(0, contratoBean.listarTodos().size());

        Cliente cliente = new Cliente("Fulano", false);
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));
        try{
            emprestimoBean.registrarEmprestimo(new Contrato(cliente));
            Assert.fail("Não pode haver um segundo empréstimo");
        }
        catch (JavacredApplicationException e){

        }

        Assert.assertEquals("O segundo empréstimo deve ser negado por não ser cliente preferencial",
                1, contratoBean.listarTodos().size());
    }

    @Test
    public void cliente_nao_preferencial_deve_ter_segundo_emprestimo_rejeitado_com_mock() {

        Assert.assertEquals(0, contratoBean.listarTodos().size());


        ContratoBean contratoBean = spy(this.contratoBean);

        ServicoAnaliseFinanceira servicoAnaliseFinanceira = mock(ServicoAnaliseFinanceira.class);
        doThrow(JavacredApplicationException.class).when(servicoAnaliseFinanceira).analisar(anyString(), anyDouble());

        emprestimoBean.config(contratoBean, servicoAnaliseFinanceira);

        Cliente cliente = new Cliente("Ciclano", false);
        Contrato contrato = new Contrato(cliente);
        try{
            emprestimoBean.registrarEmprestimo(contrato);
            Assert.fail("Não pode haver nenhum empréstimo");
        }
        catch (JavacredApplicationException e){

        }


        Assert.assertEquals("O empréstimo deve ser negado por não ser cliente preferencial",
                0, contratoBean.listarTodos().size());

        verify(contratoBean).salvar(contrato);


        //volta a implementaçãp padrão para não influenciar os outros testes
        emprestimoBean.config(this.contratoBean, this.servicoAnaliseFinanceira);
    }

    @Test
    public void cliente_preferencial_deve_ter_segundo_emprestimo_aprovado() {

        Assert.assertEquals(0, contratoBean.listarTodos().size());

        Cliente cliente = new Cliente("Beltrano", true);
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));

        Assert.assertEquals("Os dois empréstimos devem ser concedidos pois é cliente preferencial",
                2, contratoBean.listarTodos().size());

    }
}
