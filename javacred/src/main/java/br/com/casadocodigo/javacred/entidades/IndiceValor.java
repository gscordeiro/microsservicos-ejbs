package br.com.casadocodigo.javacred.entidades;

import jakarta.persistence.*;
import java.time.YearMonth;

@Entity
public class IndiceValor {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private YearMonth anoMes;
    private Double valor;

    @ManyToOne
    private Indice indice;


    public IndiceValor(){}

    public IndiceValor(Indice indice, YearMonth anoMes, Double valor) {
        this.indice = indice;
        this.anoMes = anoMes;
        this.valor = valor;
    }

    public Indice getIndice() {
        return indice;
    }

    public void setIndice(Indice indice) {
        this.indice = indice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public YearMonth getAnoMes() {
        return anoMes;
    }

    public void setAnoMes(YearMonth anoMes) {
        this.anoMes = anoMes;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
