package com.xalo.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.xalo.model.entity.Nota;
import com.xalo.model.entity.Serie;

public interface NotaRepository extends CrudRepository<Nota, Long> {

	List<Nota> findBySerie(Serie serie);
}
