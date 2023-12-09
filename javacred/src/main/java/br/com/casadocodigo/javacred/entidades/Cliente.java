package br.com.casadocodigo.javacred.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Cliente implements Serializable {

	private static final long serialVersionUID = -2380650837997961210L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private String nome;
	
	private boolean preferencial;

	@Deprecated
	public Cliente(){}

	public Cliente(String nome, boolean preferencial) {
		this.nome = nome;
		this.preferencial = preferencial;
	}

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
	private Set<Contrato> contratos = new LinkedHashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Cliente)) return false;

		Cliente cliente = (Cliente) o;

		return id.equals(cliente.id);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (nome != null ? nome.hashCode() : 0);
		result = 31 * result + (preferencial ? 1 : 0);
		return result;
	}

	public Set<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(Set<Contrato> contratos) {
		this.contratos = contratos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isPreferencial() {
		return preferencial;
	}

	public void setPreferencial(boolean preferencial) {
		this.preferencial = preferencial;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nome=" + nome + ", preferencial="
				+ preferencial + "]";
	}
}
