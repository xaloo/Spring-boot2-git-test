package com.xalo.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "nota")
public class Nota implements Serializable {

	private static final long serialVersionUID = -1935655773050935035L;
	
	@Id
	private long id;
	private BigDecimal nota;
	@ManyToOne()
    @JoinColumn(name = "fkserie")
    private Serie serie;

    public Nota() {
	}

	/* GETTERS AND SETTERS */

    public long getId() {
		return id;
	}

    public void setId(long id) {
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