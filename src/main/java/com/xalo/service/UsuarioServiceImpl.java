package com.xalo.service;

import org.springframework.stereotype.Service;

import com.xalo.model.dao.UsuarioDao;
import com.xalo.model.entity.Usuario;

@Service
public class UsuarioServiceImpl extends GenericServiceImpl<Usuario, UsuarioDao> implements UsuarioService {

	private static final long serialVersionUID = 8330043523684677609L;

}