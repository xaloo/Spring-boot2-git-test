package com.xalo.model.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

	private static final long serialVersionUID = -7995506219939261460L;

	private @Id @GeneratedValue Long id;
	@Size(max = 60)
	private String login;
	@Size(max = 60)
	private String password;


    public Usuario() {
	}

	/* GETTERS AND SETTERS */
	
	public String getLogin() {
		return login;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Usuario)) {
			return false;
		}
		return id == ((Usuario)obj).getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}