package br.com.casadocodigo.javacred.restclient.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeuContrato {

    private Integer id;
    private String descricao;
    private Double valor;
    private Integer quantidadeMeses;
    private Double taxaMensal;
    private Double saldo;
    private Date ultimaModificacao;

    private MeuCliente cliente;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeuContrato)) return false;

        MeuContrato contrato = (MeuContrato) o;

        return id.equals(contrato.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (descricao != null ? descricao.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        result = 31 * result + (quantidadeMeses != null ? quantidadeMeses.hashCode() : 0);
        result = 31 * result + (taxaMensal != null ? taxaMensal.hashCode() : 0);
        result = 31 * result + (saldo != null ? saldo.hashCode() : 0);
        result = 31 * result + (cliente != null ? cliente.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "MeuContrato{" +
				"id=" + id +
				", descricao='" + descricao + '\'' +
				", valor=" + valor +
				", quantidadeMeses=" + quantidadeMeses +
				", taxaMensal=" + taxaMensal +
				", saldo=" + saldo +
				", cliente=" + cliente +
				'}';
	}

	public Integer getId() {
			return id;
		}

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getQuantidadeMeses() {
        return quantidadeMeses;
    }

    public void setQuantidadeMeses(Integer quantidadeMeses) {
        this.quantidadeMeses = quantidadeMeses;
    }

    public Double getTaxaMensal() {
        return taxaMensal;
    }

    public void setTaxaMensal(Double taxaMensal) {
        this.taxaMensal = taxaMensal;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public MeuCliente getCliente() {
        return cliente;
    }

    public void setCliente(MeuCliente cliente) {
        this.cliente = cliente;
    }

    public Date getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(Date ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }
}
