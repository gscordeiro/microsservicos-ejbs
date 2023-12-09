package br.com.casadocodigo.javacred.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.List;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Indice {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String nome;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy = "indice", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private List<IndiceValor> valores;


    public Indice(){}

    public Indice(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Indice)) return false;

        Indice indice = (Indice) o;

        return id.equals(indice.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        return result;
    }

    public List<IndiceValor> getValores() {
        return valores;
    }

    public void setValores(List<IndiceValor> valores) {
        this.valores = valores;
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
}
