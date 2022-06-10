package com.xalo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.xalo.model.dao.GenericDao;
import com.xalo.model.dao.GenericDao.DaoFilterType;

public abstract class GenericServiceImpl<T, D extends GenericDao<T>> implements GenericService<T>, Serializable {

	private static final long serialVersionUID = -5812507159799931598L;

	@Autowired
    protected D dao;

	@Override
	public List<T> list() {
		return dao.list();
	}

	@Override
	public List<T> list(int first, int pageSize, Map<String, Boolean> order, Map<String, Object> filters, FilterType comparationType) {
		return dao.list(first, pageSize, order, filters, getDaoFilterType(comparationType));
	}

	@Override
	public int count(Map<String, Object> filters, FilterType comparationType) {
		return dao.count(filters, getDaoFilterType(comparationType));
	}

	@Override
	public BigDecimal sum(String property, Map<String, Object> filters, FilterType comparationType) {
		return dao.sum(property, filters, getDaoFilterType(comparationType));
	}

	@Override
	public T load(Long id) {
		return dao.load(id);
	}

	@Override
	public T load(Long id, boolean full) {
		return dao.load(id, full);
	}

	@Override
	public T load(String property, Object value) {
		List<T> list = dao.list(property, value);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void save(T entity) {
		dao.save(entity);
	}

	@Override
	public void delete(T entity) {
		dao.delete(entity);
	}

	private DaoFilterType getDaoFilterType(FilterType type) {
		for (DaoFilterType t : DaoFilterType.values()) {
			if (t.ordinal() == type.ordinal()) {
				return t;
			}
		}
		return null;
	}

}