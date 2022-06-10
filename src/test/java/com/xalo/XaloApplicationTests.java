package com.xalo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xalo.model.dao.UsuarioRepository;

@SpringBootTest
class XaloApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(XaloApplicationTests.class);
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	@Test
	void usuarioBCryptTest() {
		//List<Usuario> usuario_list = usuarioService.list();
		//for(Usuario usuario: usuario_list) {
		//	LOGGER.debug(usuario.getLogin());
		//}
	}
	
	
}
