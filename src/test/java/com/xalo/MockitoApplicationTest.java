package com.xalo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xalo.model.dao.UsuarioRepository;
import com.xalo.model.entity.Usuario;

@SpringBootTest
class MockitoApplicationTest {

	private static final Logger LOGGER = LogManager.getLogger(MockitoApplicationTest.class);
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Test
	void usuarioBCryptTest() {
		//Iterable<Usuario> usuario_list = usuarioRepository.findAll();
		List<Usuario> usuario_list = usuarioRepository.findByLogin("xalo");
		for (Usuario usuario: usuario_list) {
			LOGGER.info(usuario.getLogin());
		}
	}
	

}
