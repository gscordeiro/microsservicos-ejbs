package br.com.casadocodigo.javacred.control;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Indice;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Named
@ViewScoped
public class AtualizadorSaldoController implements Serializable {

	private static final long serialVersionUID = 468587460681856591L;

	@Inject
	private ContratoBean contratoBean;

	@Inject
	private GeradorContratos geradorContratos;

	private Map<String, Benchmark> benchmarks;

	private Long numeroContratos;

	private List<Indice> indices;
	
	@PostConstruct
	public void init(){
		numeroContratos = contratoBean.contaContratos();
		indices = contratoBean.buscarIndices();
		benchmarks = new HashMap<>();
	}

	public void gerarContraros(){
		geradorContratos.geraContratos();
		numeroContratos = contratoBean.contaContratos();
	}

	public void aplicaCorrecaoSincrona() throws Exception{
		Benchmark benchmark = new Benchmark(numeroContratos, "síncrono");
		benchmarks.put("sync", benchmark);
		
		double resultado = contratoBean.corrigeContratos(indices);
		benchmark.setResultado(resultado);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(benchmark.getMessage()));
	}

	public void aplicaCorrecaoAssincrona() throws Exception{
		Benchmark benchmark = new Benchmark(numeroContratos, "assíncrono");
		benchmarks.put("async", benchmark);

		Future<Double> resultado = contratoBean.corrigeContratosAsync(indices);
		benchmark.setResultadoAsync(resultado);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(benchmark.getMessage()));
	}

	public void aplicaCorrecaoParalela(){
		Benchmark benchmark = new Benchmark(numeroContratos, "paralelo");
		benchmarks.put("par", benchmark);

		double resultado = contratoBean.corrigeContratosPar(indices);
		benchmark.setResultado(resultado);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(benchmark.getMessage()));
	}

	public void aplicaCorrecaoParalelaAssincrona() throws Exception{
		
		Benchmark benchmark = new Benchmark(numeroContratos, "paralelo e assíncrono");
		benchmarks.put("parAsync", benchmark);

		Future<Double> resultado = contratoBean.corrigeContratosAsyncPar(indices);
		benchmark.setResultadoAsync(resultado);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(benchmark.getMessage()));
	}
	
	public void aplicaCorrecaoViaMES(){
		
		Benchmark benchmark = new Benchmark(numeroContratos, "MES");
		benchmarks.put("mes", benchmark);

		double resultado = contratoBean.corrigeContratosViaMES2(indices);
		benchmark.setResultado(resultado);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(benchmark.getMessage()));
	}

	public Map<String, Benchmark> getBenchmarks() {
		return benchmarks;
	}

	public Long getNumeroContratos() {
		return numeroContratos;
	}
}
