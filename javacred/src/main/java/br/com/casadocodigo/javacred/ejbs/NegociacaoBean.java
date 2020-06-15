package br.com.casadocodigo.javacred.ejbs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;

import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.FormaPagamento;
import br.com.casadocodigo.javacred.entidades.PropostaQuitacao;
import br.com.casadocodigo.javacred.exceptions.NegociacaoException;


@Stateful
@StatefulTimeout(value=30, unit=TimeUnit.SECONDS)
public class NegociacaoBean {

	private Cliente cliente;
	private double saldoDevedor;
	private List<PropostaQuitacao> propostas = new ArrayList<>();
	
	public void iniciaNegociacao(Cliente cliente){
		
		this.cliente = cliente;

		this.saldoDevedor = cliente.getContratos().stream().mapToDouble(Contrato::getSaldo).sum();
		
	}
	
	public void registrarProposta(FormaPagamento formaPagamento){
		double desconto = saldoDevedor * formaPagamento.getDesconto();
		int quantidadeDeContratos = cliente.getContratos().size();
		double valorProposto = saldoDevedor - desconto;
		propostas.add(new PropostaQuitacao(quantidadeDeContratos, saldoDevedor, valorProposto, formaPagamento));
	}
        
        
	public void registrarProposta(FormaPagamento formaPagamento, double valorProposto){
		double desconto = saldoDevedor * formaPagamento.getDesconto();
		double valorComDesconto = saldoDevedor - desconto;
                
                if(valorProposto < valorComDesconto * 0.85){
                    throw new NegociacaoException("Valor proposto menor que o mínimo");
                }
                
                int quantidadeDeContratos = cliente.getContratos().size();
		propostas.add(new PropostaQuitacao(quantidadeDeContratos, saldoDevedor, valorProposto, formaPagamento));
	}
	
	public List<PropostaQuitacao> getPropostas() {
		return propostas;
	}

	@Remove
	public void remover() {
		System.out.println("NegociacaoBean.remover()");
	}
	
	@PreDestroy
	public void preDestroy(){
		System.out.println("NegociacaoBean.preDestroy()");
	}
	
}
