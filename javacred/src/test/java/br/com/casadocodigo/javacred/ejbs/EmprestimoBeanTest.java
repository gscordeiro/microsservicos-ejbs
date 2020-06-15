package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ejb.EJBContext;
import javax.enterprise.event.Event;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmprestimoBeanTest {

    @Test
    public void cliente_preferencial_deve_ter_emprestimo_aprovado() {

        //Análise financeira deve ser "negada"
        ServicoAnaliseFinanceira saf = mock(ServicoAnaliseFinanceira.class);
        doThrow(JavacredApplicationException.class).when(saf).analisar(anyString(), anyDouble());

        EJBContext ejbContext = mock(EJBContext.class);

        ContratoBean contratoBean = mock(ContratoBean.class);

        EmprestimoBean emprestimoBean = new EmprestimoBean();
        emprestimoBean.config(contratoBean, saf);
        emprestimoBean.setEjbContext(ejbContext);

        Cliente cliente = new Cliente("Beltrano", true);
        Contrato contrato = new Contrato(cliente);

        emprestimoBean.registrarEmprestimo(contrato);

        verify(contratoBean).salvar(contrato);

        verify(ejbContext, never()).setRollbackOnly();

    }

    @Test
    public void cliente_nao_preferencial_deve_ter_emprestimo_negado() {

        //Análise financeira deve ser "negada"
        ServicoAnaliseFinanceira saf = mock(ServicoAnaliseFinanceira.class);
        doThrow(JavacredApplicationException.class).when(saf).analisar(anyString(), anyDouble());

        EJBContext ejbContext = mock(EJBContext.class);

        ContratoBean contratoBean = mock(ContratoBean.class);

        EmprestimoBean emprestimoBean = new EmprestimoBean();

        emprestimoBean.config(contratoBean, saf);
        emprestimoBean.setEjbContext(ejbContext);

        Cliente cliente = new Cliente("Fulano", false);
        Contrato contrato = new Contrato(cliente);

        try {

            emprestimoBean.registrarEmprestimo(contrato);
            fail("Aqui deve ocorrer a exceção");
        }
        catch (JavacredApplicationException e){

        }

        verify(contratoBean).salvar(contrato);

        verify(ejbContext).setRollbackOnly();

    }

}