package br.com.casadocodigo.javacred.control;


import java.util.concurrent.Future;

public class Benchmark {

    private long numeroContratos;
    private String nome;
    private Long inicio, fim;
    private double resultado;
    Future<Double> resultadoAsync;

    public Benchmark(long numeroContratos, String nome) {
        this.numeroContratos = numeroContratos;
        this.nome = nome;
        this.inicio = System.currentTimeMillis();
    }

    public void stop(){
        this.fim = System.currentTimeMillis();
    }

    public long getDuracao(){
        return fim - inicio;
    }

    public String getMessage(){
        if(fim == null) stop();

        return String.format("%d contratos atualizados (%s) em %d ms", numeroContratos, nome, getDuracao());
    }

    public void setResultado(double resultado) {
        this.resultado = resultado;
    }

    public double getResultado() {
        return resultado;
    }

    public void setResultadoAsync(Future<Double> resultadoAsync) {
        this.resultadoAsync = resultadoAsync;
    }

    public Future<Double> getResultadoAsync() {
        return resultadoAsync;
    }
}
