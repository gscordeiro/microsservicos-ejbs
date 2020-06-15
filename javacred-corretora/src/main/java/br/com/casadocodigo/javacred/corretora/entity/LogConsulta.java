package br.com.casadocodigo.javacred.corretora.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class LogConsulta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String codigoAtivo;
    private double valorAtivo;
    private double quantidade;
    private Date data;
    private String ip;

    @Deprecated
    public LogConsulta(){}

    public LogConsulta(String codigoAtivo, double valorAtivo, double quantidade, String ip) {
        this.codigoAtivo = codigoAtivo;
        this.valorAtivo = valorAtivo;
        this.quantidade = quantidade;
        this.data = new Date();
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "LogConsulta{" +
                "id=" + id +
                ", codigoAtivo='" + codigoAtivo + '\'' +
                ", valorAtivo=" + valorAtivo +
                ", quantidade=" + quantidade +
                ", data=" + data +
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

    public double getValorAtivo() {
        return valorAtivo;
    }

    public void setValorAtivo(double valorAtivo) {
        this.valorAtivo = valorAtivo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
