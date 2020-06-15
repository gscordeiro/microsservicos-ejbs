package br.com.casadocodigo.javacred.rest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
@XmlRootElement
public class Cotacao {

    public enum Moeda {DOLAR, EURO, REAL}

    private Date data;
    private Integer quantidade;
    private Double valor;
    private Moeda moedaCotada;
    private Moeda moedaValor;

    public Cotacao(){}
    public Cotacao(Integer quantidade, Double valor, Moeda moedaCotada, Moeda moedaValor) {
        this.data = new Date();
        this.quantidade = quantidade;
        this.valor = valor;
        this.moedaCotada = moedaCotada;
        this.moedaValor = moedaValor;
    }

    @Override
    public String toString() {
        return "Cotacao{" +
                "moedaCotada=" + moedaCotada +
                ", data=" + data +
                ", quantidade=" + quantidade +
                ", valor=" + valor +
                ", moedaValor=" + moedaValor +
                '}';
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Moeda getMoedaCotada() {
        return moedaCotada;
    }

    public void setMoedaCotada(Moeda moedaCotada) {
        this.moedaCotada = moedaCotada;
    }

    public Moeda getMoedaValor() {
        return moedaValor;
    }

    public void setMoedaValor(Moeda moedaValor) {
        this.moedaValor = moedaValor;
    }
}
