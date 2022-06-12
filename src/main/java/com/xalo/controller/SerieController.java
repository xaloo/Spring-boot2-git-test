package com.xalo.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xalo.model.dao.SerieRepository;
import com.xalo.model.entity.Serie;

@Controller
public class SerieController {
	
	private static final Logger LOGGER = LogManager.getLogger(SerieController.class);
	
	//Podemos sustituir @Controller y @ResponseBOdy con @RestController directamente.
	@Autowired
	private SerieRepository serieRepository; 
	
	@GetMapping("/listaSeries") 
	@ResponseBody
	public void ListaSeries() {
		Iterable<Serie> listaSeries = serieRepository.findAll();
		for (Serie serie: listaSeries) {
			LOGGER.info("Año: "+ serie.getAnyo() +" Nombre: "+serie.getNombre());
		} 
	}
	
}