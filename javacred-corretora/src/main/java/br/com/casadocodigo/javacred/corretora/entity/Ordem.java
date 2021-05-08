package br.com.casadocodigo.javacred.corretora.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

import static br.com.casadocodigo.javacred.corretora.entity.Ordem.BUSCAR_DATA_ULTIMA_ALTERACAO;
import static br.com.casadocodigo.javacred.corretora.entity.Ordem.BUSCAR_HASH;

@Entity
@NamedQueries({
        @NamedQuery(name = BUSCAR_DATA_ULTIMA_ALTERACAO, query = "select o.ultimaModificacao from Ordem o where o.id = :id"),
        @NamedQuery(name = BUSCAR_HASH, query = "select o.hash from Ordem o where o.id = :id")
})
public class Ordem {


    public static final String BUSCAR_DATA_ULTIMA_ALTERACAO = "Ordem.buscarDataUltimaAlteracao";
    public static final String BUSCAR_HASH = "Ordem.buscarHash";

    public enum Tipo {COMPRA, VENDA}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Tipo tipo;
    private String codigoAtivo;
    private double quantidade;
    private double valorAtivo;
    private double valorTotal;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date ultimaModificacao;
    private int hash;
    private String ip;

    @Deprecated
    public Ordem(){}

    public Ordem(Tipo tipo, String codigoAtivo, double quantidade, double valorAtivo, String ip) {
        this.tipo = tipo;
        this.codigoAtivo = codigoAtivo;
        this.quantidade = quantidade;
        this.valorAtivo = valorAtivo;
        this.valorTotal = valorAtivo * quantidade;
        this.ip = ip;
        this.ultimaModificacao = new Date();
    }

    @PrePersist
    @PreUpdate
    public void updateBean(){
        System.out.println(">>>>>>> Ordem.updateBean");
        hash = this.hashCode();
        ultimaModificacao = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ordem)) return false;

        Ordem ordem = (Ordem) o;

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
        return "Ordem{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", codigoAtivo='" + codigoAtivo + '\'' +
                ", quantidade=" + quantidade +
                ", valorAtivo=" + valorAtivo +
                ", valorTotal=" + valorTotal +
                ", ultimaModificacao=" + ultimaModificacao +
                ", hash=" + hash +
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

    public int getHash() {
        return hash;
    }
}
