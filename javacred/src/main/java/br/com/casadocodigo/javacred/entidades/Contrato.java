package br.com.casadocodigo.javacred.entidades;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.YearMonth;
import java.util.Date;

import static br.com.casadocodigo.javacred.entidades.Contrato.BUSCAR_HASH_CONTRATO;
import static br.com.casadocodigo.javacred.entidades.Contrato.BUSCAR_ULTIMA_ALTERACAO_CONTRATO;

@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = BUSCAR_ULTIMA_ALTERACAO_CONTRATO, query = "select c.ultimaModificacao from Contrato c where c.id = :id"),
    @NamedQuery(name = BUSCAR_HASH_CONTRATO, query = "select c.hash from Contrato c where c.id = :id")
})
public class Contrato implements Serializable {

	private static final long serialVersionUID = 7406472733993782440L;

	public static final String BUSCAR_ULTIMA_ALTERACAO_CONTRATO = "Contrato.buscarUltimaAlteracaoContrato";
	public static final String BUSCAR_HASH_CONTRATO = "Contrato.buscarHashContrato";

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String descricao;
	private Double valor;
	private Integer quantidadeMeses;
	private Double taxaMensal;
	private Double saldo;
	private YearMonth ultimaCorrecao;
	private Date ultimaModificacao;
	private int hash;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
	private Indice indiceCorrecao;

	@ManyToOne(cascade=CascadeType.MERGE)
	private Cliente cliente;

	public Contrato() {
	}
	
	public Contrato(Cliente cliente) {
		this.cliente = cliente;
		this.valor = 0.0;
	}

	@PrePersist
    @PreUpdate
	public void updateBean(){
        System.out.println(">>>>>>> Contrato.updateBean");
        hash = this.hashCode();
        ultimaModificacao = new Date();
        if(saldo == null)  saldo = valor;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Contrato)) return false;

		Contrato contrato = (Contrato) o;

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

        System.out.println("Contrato.hashCode >>> " + result + " >>>> " + this);
        return result;
    }

    @Override
    public String toString() {
        return "Contrato{" +
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

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getQuantidadeMeses() {
		return quantidadeMeses;
	}

	public void setQuantidadeMeses(Integer meses) {
		this.quantidadeMeses = meses;
	}

	public Double getTaxaMensal() {
		return taxaMensal;
	}

	public void setTaxaMensal(Double taxa) {
		this.taxaMensal = taxa;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Indice getIndiceCorrecao() {
		return indiceCorrecao;
	}

	public void setIndiceCorrecao(Indice indiceCorrecao) {
		this.indiceCorrecao = indiceCorrecao;
	}


    public YearMonth getUltimaCorrecao() {
        return ultimaCorrecao;
    }

    public void setUltimaCorrecao(YearMonth ultimaAtualizacao) {
        this.ultimaCorrecao = ultimaAtualizacao;
    }

	public Date getUltimaModificacao() {
		return ultimaModificacao;
	}

	public void setUltimaModificacao(Date ultimaAlteracao) {
		this.ultimaModificacao = ultimaAlteracao;
	}

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }
}
