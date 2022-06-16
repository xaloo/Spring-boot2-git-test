package com.xalo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xalo.exception.SerieNotFoundException;
import com.xalo.model.dao.SerieJpaRepository;
import com.xalo.model.entity.Serie;

@RestController
public class RestAPISerieController {

	private final SerieJpaRepository repository;

	RestAPISerieController(SerieJpaRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/series")
	List<Serie> all() {
		return repository.findAll();
	}

	@PostMapping("/series")
	Serie newEmployee(@RequestBody Serie newSerie) {
		return repository.save(newSerie);
	}
	
	@GetMapping("/series/{id}")
	Serie one(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new SerieNotFoundException(id));
	}

	@PutMapping("/series/{id}")
	Serie replaceEmployee(@RequestBody Serie newSerie, @PathVariable Long id) {

		//Sobreescribir los datos. Si no existe guardamos una nueva
		return repository.findById(id).map(serie -> {
			serie.setAnyo(newSerie.getAnyo());
			serie.setNombre(newSerie.getNombre());
			serie.setDirector(newSerie.getDirector());
			serie.setNotas(newSerie.getNotas());
			return repository.save(serie);
		}).orElseGet(() -> {
			newSerie.setId(id);
			return repository.save(newSerie);
		});
	}

	@DeleteMapping("/series/{id}")
	void deleteSerie(@PathVariable Long id) {
		repository.deleteById(id);
	}

}