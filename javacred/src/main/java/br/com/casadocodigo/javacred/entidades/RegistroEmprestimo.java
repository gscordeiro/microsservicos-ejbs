package br.com.casadocodigo.javacred.entidades;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class RegistroEmprestimo {
	
	public enum Tipo {CONTRATACAO, QUITACAO}

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String tomador;
	private Double valor;
	@Temporal(TemporalType.DATE)
	private Date data = new Date();
	private Tipo tipo;
	
	
	public RegistroEmprestimo() {
	}

	public RegistroEmprestimo(String tomador, Double valor, Tipo tipo) {
		this.tomador = tomador;
		this.valor = valor;
		this.tipo = tipo;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	public String getTomador() {
		return tomador;
	}

	public void setTomador(String tomador) {
		this.tomador = tomador;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
}
