package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.RegistroEmprestimo;
import br.com.casadocodigo.javacred.exceptions.JavacredApplicationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoAnaliseFinanceiraTest {


    @Test
    public void deve_lancar_excecao_se_ja_tem_emprestimo() throws Exception {

        List<RegistroEmprestimo> emprestimos = new ArrayList<>();
        emprestimos.add(new RegistroEmprestimo());


        ServicoAnaliseFinanceira saf = spy(new ServicoAnaliseFinanceira());

        doReturn(emprestimos).when(saf).buscarEmprestimos(anyString(), any(Date.class));

        assertThrows(JavacredApplicationException.class, () -> {
            saf.analisar("Fulano", 10_000.0);
        });


    }

    @Test
    public void nao_deve_lancar_excecao_se_nao_tem_emprestimo() throws Exception {

        ServicoAnaliseFinanceira saf = spy(new ServicoAnaliseFinanceira());
        EntityManager entityManager = mock(EntityManager.class);
        saf.setEntityManager(entityManager);

        List<RegistroEmprestimo> emprestimos = new ArrayList<>();

        doReturn(emprestimos).when(saf).buscarEmprestimos(anyString(), any(Date.class));

        saf.analisar("Fulano", 10_000.0);

        ArgumentCaptor<RegistroEmprestimo> registroEmprestimoCaptor = ArgumentCaptor.forClass(RegistroEmprestimo.class);
        verify(entityManager).persist(registroEmprestimoCaptor.capture());

        RegistroEmprestimo emprestimoRegistrado = registroEmprestimoCaptor.getValue();

        assertEquals("Fulano", emprestimoRegistrado.getTomador());
        assertEquals(10_000.0, emprestimoRegistrado.getValor(), 0.001);

    }

}