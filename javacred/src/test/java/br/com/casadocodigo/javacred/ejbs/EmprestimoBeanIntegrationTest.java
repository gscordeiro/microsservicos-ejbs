package br.com.casadocodigo.javacred.ejbs;


import br.com.casadocodigo.javacred.entidades.*;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;
import br.com.casadocodigo.javacred.exceptions.JavacredException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import static org.mockito.Mockito.*;


@ExtendWith(ArquillianExtension.class)
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
//                        Maven.resolver().resolve("org.mockito:mockito-core:5.8.0").withTransitivity().asFile()
                        Maven.resolver().resolve("org.mockito:mockito-all:1.10.19").withTransitivity().asFile()
                )
                .addAsManifestResource(new StringAsset("Dependencies: jdk.unsupported\n" /* required by Mockito */), "MANIFEST.MF");

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

    @BeforeEach
    public void limparDados() throws Exception {
        utx.begin();
        em.createQuery("delete from Contrato ").executeUpdate();
        em.createQuery("delete from RegistroEmprestimo").executeUpdate();
        utx.commit();
    }

    @Test
    public void cliente_nao_preferencial_deve_ter_segundo_emprestimo_rejeitado() {

        Assertions.assertEquals(0, contratoBean.listarTodos().size());

        Cliente cliente = new Cliente("Fulano", false);
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));
        try{
            emprestimoBean.registrarEmprestimo(new Contrato(cliente));
            Assertions.fail("Não pode haver um segundo empréstimo");
        }
        catch (JavacredApplicationException e){

        }

        Assertions.assertEquals(1, contratoBean.listarTodos().size(), "O segundo empréstimo deve ser negado por não ser cliente preferencial");
    }

    @Test
    public void cliente_nao_preferencial_deve_ter_segundo_emprestimo_rejeitado_com_mock() {

        Assertions.assertEquals(0, contratoBean.listarTodos().size());


        ContratoBean contratoBean = spy(this.contratoBean);

        ServicoAnaliseFinanceira servicoAnaliseFinanceira = mock(ServicoAnaliseFinanceira.class);
        doThrow(JavacredApplicationException.class).when(servicoAnaliseFinanceira).analisar(anyString(), anyDouble());

        emprestimoBean.config(contratoBean, servicoAnaliseFinanceira);

        Cliente cliente = new Cliente("Ciclano", false);
        Contrato contrato = new Contrato(cliente);
        try{
            emprestimoBean.registrarEmprestimo(contrato);
            Assertions.fail("Não pode haver nenhum empréstimo");
        }
        catch (JavacredApplicationException e){

        }


        Assertions.assertEquals(0, contratoBean.listarTodos().size(), "O empréstimo deve ser negado por não ser cliente preferencial");

        verify(contratoBean).salvar(contrato);


        //volta a implementaçãp padrão para não influenciar os outros testes
        emprestimoBean.config(this.contratoBean, this.servicoAnaliseFinanceira);
    }

    @Test
    public void cliente_preferencial_deve_ter_segundo_emprestimo_aprovado() {

        Assertions.assertEquals(0, contratoBean.listarTodos().size());

        Cliente cliente = new Cliente("Beltrano", true);
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));
        emprestimoBean.registrarEmprestimo(new Contrato(cliente));

        Assertions.assertEquals(2, contratoBean.listarTodos().size(), "Os dois empréstimos devem ser concedidos pois é cliente preferencial");

    }
}
