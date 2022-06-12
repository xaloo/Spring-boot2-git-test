package com.xalo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UsernameController {
	
	//Podemos sustituir @Controller y @ResponseBOdy con @RestController directamente.
	
	@GetMapping("/username") 
	@ResponseBody
	public String currentUserName(Authentication authentication) {
		return authentication.getName();
	}
	
}