package br.com.casadocodigo.javacred.corretora.test.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdemTO {

    public enum Tipo {COMPRA, VENDA}

    private Integer id;
    private Tipo tipo;
    private String codigoAtivo;
    private double quantidade;
    private double valorAtivo;
    private double valorTotal;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date ultimaModificacao;
    private String ip;

    @Deprecated
    public OrdemTO(){}

    public OrdemTO(Tipo tipo, String codigoAtivo, double quantidade, double valorAtivo, String ip) {
        this.tipo = tipo;
        this.codigoAtivo = codigoAtivo;
        this.quantidade = quantidade;
        this.valorAtivo = valorAtivo;
        this.valorTotal = valorAtivo * quantidade;
        this.ip = ip;
        this.ultimaModificacao = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemTO)) return false;

        OrdemTO ordem = (OrdemTO) o;

        return id.equals(ordem.id);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tipo != null ? tipo.ordinal() : 0);
        result = 31 * result + (codigoAtivo != null ? codigoAtivo.hashCode() : 0);
        temp = Double.doubleToLongBits(quantidade);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valorAtivo);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valorTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrdemTO{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", codigoAtivo='" + codigoAtivo + '\'' +
                ", quantidade=" + quantidade +
                ", valorAtivo=" + valorAtivo +
                ", valorTotal=" + valorTotal +
                ", ultimaModificacao=" + ultimaModificacao +
                ", ip='" + ip + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public double getValorAtivo() {
        return valorAtivo;
    }

    public void setValorAtivo(double valorAtivo) {
        this.valorAtivo = valorAtivo;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }


    public Date getUltimaModificacao() {
        return ultimaModificacao;
    }

}
