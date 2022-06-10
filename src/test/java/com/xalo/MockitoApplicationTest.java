package com.xalo;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xalo.model.dao.UsuarioRepository;
import com.xalo.model.entity.Usuario;

@SpringBootTest
class MockitoApplicationTest {

	private static final Logger LOGGER = LogManager.getLogger(MockitoApplicationTest.class);
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Mock
	private Usuario usuarioMock;
	
	@Test
	void usuarioBCryptTest() {
		
		when(usuarioMock.getPassword()).thenReturn("$2a$12$aJ/8LbbdHHLbKGNs6k24huBwKIul.Pd0xnagAM9HO3bj76z9Hhmqy");
		
		//Iterable<Usuario> usuario_list = usuarioRepository.findAll();
		List<Usuario> usuario_list = usuarioRepository.findByLogin("xalo");
		for (Usuario usuario: usuario_list) {
			LOGGER.info(usuario.getLogin());
		}
		
		assertTrue(usuario_list.get(0).getPassword().equals(usuarioMock.getPassword()));
	}
	

}
