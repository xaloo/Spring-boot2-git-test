package com.xalo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.xalo.model.dao.UsuarioRepository;
import com.xalo.model.entity.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserDetails userDet = null;
		//Solo puede haber 1 usuario, clave primario
		List<Usuario> usuarios_list = usuarioRepository.findByLogin(username);
		if (!usuarios_list.isEmpty() && usuarios_list.size() > 0) {
			Usuario usuario = usuarios_list.get(0);
			//TODO Aquí buscaremos en una tabla de credenciales BBDD
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
			roles.add(new SimpleGrantedAuthority("ADMIN"));
			userDet = new User(usuario.getLogin(),usuario.getPassword(), roles);
		}
		return userDet;
	}


}
