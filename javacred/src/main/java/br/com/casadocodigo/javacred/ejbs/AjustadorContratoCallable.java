package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Contrato;

import java.util.concurrent.Callable;

public class AjustadorContratoCallable implements Callable<Double> {
	
	private Contrato contrato;
	private double percentual;
	
	private AjustadorContratoBean ajustadorContratoBean;
	

	public AjustadorContratoCallable(Contrato contrato, double percentual, AjustadorContratoBean ajustadorContratoBean) {
		this.contrato = contrato;
		this.percentual = percentual;
		this.ajustadorContratoBean = ajustadorContratoBean;
	}

	@Override
	public Double call() throws Exception {

		System.out.println("executando*****>>> " + Thread.currentThread());
		return ajustadorContratoBean.ajustaContratoPercentualDireto(contrato, percentual);
	}

}
