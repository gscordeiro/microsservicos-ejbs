package br.com.casadocodigo.javacred.corretora.cliente;

public class Consulta {
    private String codigoAtivo;
    private double quantidade;

    @Deprecated
    public Consulta(){}

    public Consulta(String codigoAtivo, double quantidade) {
        this.codigoAtivo = codigoAtivo;
        this.quantidade = quantidade;
    }

    public String getCodigoAtivo() {
        return codigoAtivo;
    }

    public void setCodigoAtivo(String codigoAtivo) {
        this.codigoAtivo = codigoAtivo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
