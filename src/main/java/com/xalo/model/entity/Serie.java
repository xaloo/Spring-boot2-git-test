package com.xalo.model.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "serie")
public class Serie implements Serializable {

	private static final long serialVersionUID = 4740519942103214355L;

	@Id
	private long id;
	@Size(max = 50)
	private String director;
	@Size(max = 50)
	private String nombre;
	private int anyo;
	
	/* Listas*/
	@OneToMany(mappedBy="nota")
	private List<Nota> notas;
	
    public Serie() {
	}

	/* GETTERS AND SETTERS */

    public long getId() {
		return id;
	}

    public void setId(long id) {
		this.id = id;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getAnyo() {
		return anyo;
	}

	public void setAnyo(int anyo) {
		this.anyo = anyo;
	}

	public List<Nota> getNotas() {
		return notas;
	}

	public void setNotas(List<Nota> notas) {
		this.notas = notas;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Serie)) {
			return false;
		}
		return id == ((Serie)obj).getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}