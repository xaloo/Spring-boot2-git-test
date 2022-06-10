package com.xalo.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.xalo.model.entity.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	List<Usuario> findByLogin(String login);

	Usuario findById(long id);
}
