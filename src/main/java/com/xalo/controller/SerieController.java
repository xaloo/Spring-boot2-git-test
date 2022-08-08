package com.xalo.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xalo.model.dao.SerieJpaRepository;
import com.xalo.model.dao.SerieRepository;
import com.xalo.model.entity.Serie;

@Controller
public class SerieController {
	
	private static final Logger LOGGER = LogManager.getLogger(SerieController.class);
	
	//Podemos sustituir @Controller y @ResponseBOdy con @RestController directamente.
	@Autowired
	private SerieRepository serieRepository; 
	
	@Autowired
	private SerieJpaRepository serieJpaRepository; 
	
	@GetMapping("/nota") 
	@ResponseBody
	public void obtenerNotaSerie(@RequestParam(name="nombre", required = true) String nombre) {
		Iterable<Serie> listaSeries = serieJpaRepository.findAll();
		for (Serie serie: listaSeries) {
			LOGGER.info("Año: "+ serie.getAnyo() +" Nombre: "+serie.getNombre()); 
			LOGGER.info("test");
		} 
	}
	
	@GetMapping("/listaSeries") 
	@ResponseBody
	public void ListaSeries() {
		Iterable<Serie> listaSeries = serieRepository.findAll();
		for (Serie serie: listaSeries) {
			LOGGER.info("Año: "+ serie.getAnyo() +" Nombre: "+serie.getNombre());
		} 
	}
	
	@GetMapping("/listaSeriesJpa") 
	@ResponseBody
	public void ListaSeriesJpa() {
		List<Serie> listaSeries = serieJpaRepository.findAll();
		for (Serie serie: listaSeries) {
			LOGGER.info("Año: "+ serie.getAnyo() +" Nombre: "+serie.getNombre());
		} 
	}
	
}
