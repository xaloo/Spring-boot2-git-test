package com.xalo.model.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GenericDao<T> {

	public enum DaoFilterType {
		CONTAINS, STARTWITH, ENDSWITH, EXACT;
	}

	T load(Long id);
	T load(Long id, boolean full);
	T load(String property, Object value);
	List<T> list();
	List<T> list(String hql);
	List<T> list(String property, Object value);
	List<T> list(int first, int pageSize, Map<String, Boolean> order, Map<String, Object> filters, DaoFilterType comparationType);
	BigDecimal avg(String property, Map<String, Object> filters, DaoFilterType comparationType);
	int count(Map<String, Object> filters, DaoFilterType comparationType);
	BigDecimal max(String property, Map<String, Object> filters, DaoFilterType comparationType);
	BigDecimal min(String property, Map<String, Object> filters, DaoFilterType comparationType);
	BigDecimal sum(String property, Map<String, Object> filters, DaoFilterType comparationType);
	void save(T entity);
	void delete(T entity);
}