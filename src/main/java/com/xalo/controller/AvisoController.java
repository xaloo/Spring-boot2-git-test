package com.xalo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AvisoController {
	
	@GetMapping("/aviso")
	public String aviso(Model model) {
		model.addAttribute("name", "Prueba template");
		return "aviso";
	}	
}