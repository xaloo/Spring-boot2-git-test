package com.xalo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public interface GenericService<T> {

	public enum FilterType {
		CONTAINS, STARTWITH, ENDSWITH, EXACT;
	}

	@Transactional(readOnly = true)
	List<T> list();

	@Transactional(readOnly = true)
	List<T> list(int first, int pageSize, Map<String, Boolean> order, Map<String, Object> filters, FilterType comparationType);

	@Transactional(readOnly = true)
	int count(Map<String, Object> filters, FilterType comparationType);

	@Transactional(readOnly = true)
	BigDecimal sum(String property, Map<String, Object> filters, FilterType comparationType);

	@Transactional(readOnly = true)
	T load(Long id);

	@Transactional(readOnly = true)
	T load(Long id, boolean full);

	@Transactional(readOnly = true)
	T load(String property, Object value);

	@Transactional
	void save(T entity);

	@Transactional
	void delete(T entity);

}