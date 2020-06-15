package br.com.casadocodigo.javacred.entidades;

import java.io.Serializable;
import java.util.Date;

public class PropostaQuitacao implements Serializable {

	private static final long serialVersionUID = -7797391569312012044L;
	
	private Date dataProposta;
	private Integer quantidadeContratos;
	private Double saldoOriginal;
	private Double valorProposto;
	private FormaPagamento formaPagamento;
	
	
	public PropostaQuitacao(Integer quantidadeContratos, Double saldoOriginal, Double valorProposto, FormaPagamento formaPagamento) {
		
		this.dataProposta = new Date();
		this.quantidadeContratos = quantidadeContratos;
		this.saldoOriginal = saldoOriginal;
		this.valorProposto = valorProposto;
		this.formaPagamento = formaPagamento;
	}


	@Override
	public String toString() {
		return "PropostaQuitacao [dataProposta=" + dataProposta
				+ ", quantidadeContratos=" + quantidadeContratos
				+ ", saldoOriginal=" + saldoOriginal + ", valorProposto="
				+ valorProposto + ", formaPagamento=" + formaPagamento + "]";
	}
	
	
}
