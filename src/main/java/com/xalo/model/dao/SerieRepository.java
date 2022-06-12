package com.xalo.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.xalo.model.entity.Serie;

public interface SerieRepository extends CrudRepository<Serie, Long> {

	List<Serie> findByDirector(String director);
	
	List<Serie> findByAnyo(Integer anyo);

	Serie findById(long id);
}
