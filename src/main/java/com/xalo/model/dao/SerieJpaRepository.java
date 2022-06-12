package com.xalo.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xalo.model.entity.Serie;

public interface SerieJpaRepository extends JpaRepository<Serie, Long> {
	
}
