package com.xalo.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xalo.model.entity.Nota;

public interface NotaJpaRepository extends JpaRepository<Nota, Long> {
	
}
