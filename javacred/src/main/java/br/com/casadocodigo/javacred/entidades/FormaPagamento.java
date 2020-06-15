package br.com.casadocodigo.javacred.entidades;

public enum FormaPagamento {
	
	VISTA(30), ATE_6_VEZES(20), MAIS_6_VEZES(10);
	
	private double desconto;
	
	FormaPagamento(double percentual) {
		this.desconto = percentual/100;
	}

	public double getDesconto() {
		return desconto;
	}
}
