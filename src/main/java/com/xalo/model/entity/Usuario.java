package com.xalo.model.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

	private static final long serialVersionUID = -7995506219939261460L;

	@Id
	@GenericGenerator(name = "usuario_sequence", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "usuario_sequence")})
	@GeneratedValue(generator = "usuario_sequence")
	private long id;
	@Size(max = 60)
	private String login;
	@Size(max = 60)
	private String password;


    public Usuario() {
	}

	/* GETTERS AND SETTERS */

    public long getId() {
		return id;
	}

    public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
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