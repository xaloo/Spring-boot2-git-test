package com.xalo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xalo.model.dao.GenericDaoImpl;
import com.xalo.model.entity.Usuario;
import com.xalo.service.UsuarioService;

@SpringBootTest
class XaloApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(GenericDaoImpl.class);
	
	@Autowired
	private UsuarioService usuarioService;
	
	
	@Test
	void usuarioBCryptTest() {
		List<Usuario> usuario_list = usuarioService.list();
		for(Usuario usuario: usuario_list) {
			LOGGER.debug(usuario.getLogin());
		}
	}
	
	
}
