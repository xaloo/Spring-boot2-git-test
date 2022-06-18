package com.xalo.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "nota")
public class Nota implements Serializable {

	private static final long serialVersionUID = -1935655773050935035L;
	
	private @Id @GeneratedValue Long id;
	private BigDecimal nota;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkserie")
	@JsonIgnoreProperties("notas") //Needed to ignore Json loop
    private Serie serie;

    public Nota() {
	}

	/* GETTERS AND SETTERS */
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getNota() {
		return nota;
	}

	public void setNota(BigDecimal nota) {
		this.nota = nota;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Nota)) {
			return false;
		}
		return id == ((Nota)obj).getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}