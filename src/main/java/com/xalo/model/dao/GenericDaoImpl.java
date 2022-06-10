package com.xalo.model.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;

import com.xalo.util.ObjectUtil;

@Repository
public abstract class GenericDaoImpl<T> implements GenericDao<T>, Serializable {

	private static final long serialVersionUID = 3342361240964040755L;

	private static final Logger LOGGER = LogManager.getLogger(GenericDaoImpl.class);

	private static boolean initializedSession = false;

	@Autowired
	private SessionFactory sessionFactory;

	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericDaoImpl.class)[0];
		}
		return entityClass;
	}

	private void initSession() {
		try {
			if (!initializedSession) {
				//sessionFactory.getCurrentSession().createSQLQuery("ALTER SESSION SET NLS_SORT = BINARY").executeUpdate();
				sessionFactory.getCurrentSession().createSQLQuery("ALTER SESSION SET NLS_SORT = SPANISH").executeUpdate();
				initializedSession = true;
			}
		} catch (Exception e) {
			LOGGER.error("Error en initSession: " + e.getMessage(), e);
		}
	}

	protected Session getSession() {
		initSession();
		return sessionFactory.getCurrentSession();
	}

	@Override
	public BigDecimal avg(String property, Map<String, Object> filters, DaoFilterType comparationType) {
		return projectionDecimal(Projections.avg(property), filters, comparationType);
	}

	@Override
	public int count(Map<String, Object> filters, DaoFilterType comparationType) {
		return projectionInteger(Projections.countDistinct(Projections.id().toString()), filters, comparationType);
	}

	@Override
	public BigDecimal max(String property, Map<String, Object> filters, DaoFilterType comparationType) {
		return projectionDecimal(Projections.max(property), filters, comparationType);
	}

	@Override
	public BigDecimal min(String property, Map<String, Object> filters, DaoFilterType comparationType) {
		return projectionDecimal(Projections.min(property), filters, comparationType);
	}

	@Override
	public BigDecimal sum(String property, Map<String, Object> filters, DaoFilterType comparationType) {
		return projectionDecimal(Projections.sum(property), filters, comparationType);
	}

	private int projectionInteger(Projection projection, Map<String, Object> filters, DaoFilterType comparationType) {
		Object result = projection(projection, filters, comparationType);
		if (result == null) {
			return 0;
		} else if (result instanceof Integer) {
			return ((Integer) result);
		} else if (result instanceof BigDecimal) {
			return ((BigDecimal) result).intValue();
		} else {
			return ((Long) result).intValue();
		}
	}

	private BigDecimal projectionDecimal(Projection projection, Map<String, Object> filters, DaoFilterType comparationType) {
		Object result = projection(projection, filters, comparationType);
		if (result == null) {
			return BigDecimal.ZERO;
		} else if (result instanceof Float) {
			return new BigDecimal((Float)result);
		} else if (result instanceof Double) {
			return new BigDecimal((Double)result);
		} else {
			return (BigDecimal) result;
		}
	}

	private Object projection(Projection projection, Map<String, Object> filters, DaoFilterType comparationType) {
		try {
			LOGGER.debug("GenericDao.projection");
			Criteria criteria = getSession().createCriteria(getEntityClass()).setProjection(projection);
			Set<String> aliasList = new HashSet<String>();
			addFilters(criteria, filters, comparationType, aliasList);
			addAlias(criteria, aliasList);
			Object result =  criteria.uniqueResult();
			LOGGER.debug("resultado " + result + " (" + getEntityClass().getName() + ")");
			return result;
		} catch (Exception e) {
			LOGGER.error("Error en projection: " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public T load(Long id) {
		/*final T entity = (T)getSession().get(getEntityClass(), id);
		if (entity != null) {
			LOGGER.debug("entidad recuperada");
		}
		return entity;*/
		return load(id, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T load(Long id, boolean full) {
		final T entity = (T)getSession().get(getEntityClass(), id);

		if (entity != null) {
			LOGGER.debug("entidad recuperada");

			if (full) {
				try {
					LOGGER.debug("inicializamos la entidad: " + entity.getClass().getName());
					Method[] metodos = entity.getClass().getMethods();
					for (int i = 0; i < metodos.length; i++) {
						Method metodo = metodos[i];
						if (metodo.getReturnType().equals(Set.class)) {
							if (metodo.getParameterTypes().length == 0 && !metodo.isAnnotationPresent(Transient.class)) { //Los get de las listas no tiene que tener parametros ni la anotacion transient para que sean realmente gets de atributos de la entidad
								LOGGER.debug("inicializamos la lista: " + metodo.getName());
								((Set<T>)metodo.invoke(entity, new Object[0])).size();
							}
						}
						if (metodo.getReturnType().equals(List.class)) {
							if (metodo.getParameterTypes().length == 0 && !metodo.isAnnotationPresent(Transient.class)) { //Los get de las listas no tiene que tener parametros ni la anotacion transient para que sean realmente gets de atributos de la entidad
								LOGGER.debug("inicializamos la lista: " + metodo.getName());
								((List<T>)metodo.invoke(entity, new Object[0])).size();
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("error en load: " + e.getMessage(), e);
		    		return null;
				}
			}
		}
		return entity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T load(String property, Object value) {
		try {
			LOGGER.debug("GenericDao.load(" + property + "," + value + ")");    
			Criteria criteria = getSession().createCriteria(getEntityClass());
			criteria.add(Restrictions.eq(property, value));
			criteria.addOrder(Order.asc(property).ignoreCase());
			return (T)criteria.uniqueResult();
		} catch (Exception e) {
			LOGGER.error("error en load: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void save(T entity) {
		long id = (Long)ObjectUtil.getId(entity);
        try {
    		// Convierto a null las cadenas vacias porque aunque la entidad tenga la anotacion @SelectBeforeUpdate, al comparar cadena vacia (Java) con null (Oracle), 
        	// detecta que son valores distintos y realiza un update innecesario. Ademas, si esta activa la auditoria, crea una nueva revision aunque la entidad no haya cambiado
        	ObjectUtil.emptyStringsToNull(entity);
        	// Guardamos el objeto en la base de datos
    		getSession().saveOrUpdate(entity);
    		getSession().flush();
    		LOGGER.debug("Guardado correctamente");
        } catch (RuntimeException e) {
        	ObjectUtil.setId(entity, id); // Evita que al fallar un insert actualice la clave primaria con un nuevo valor de la secuencia
            LOGGER.error("Error en save: " +  e.getMessage(), e);
            throw e;
        }
	}

	@Override
	public void delete(T entity) {
		// Guardamos el objeto en la base de datos
		getSession().delete(entity);
		LOGGER.debug("Borrado correctamente");
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list() {
		try {
			LOGGER.debug("GenericDao.list()");
			final List<T> entities = getSession().createCriteria(getEntityClass()).list();
			LOGGER.debug("recuperadas " + entities.size() + " entidades (" + getEntityClass().getName() + ")");
			return entities;
		} catch (Exception e) {
			LOGGER.error("error en list(): " + e.getMessage(), e);
			return new ArrayList<T>();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(String hql) {
		List<T> entities = null;
		try {
			LOGGER.debug("GenericDao.list(" + hql + ")");
			entities = getSession().createQuery(hql).list();
		} catch (Exception e) {
			LOGGER.error("error en list: " + e.getMessage(), e);
			entities = new ArrayList<T>();
		}
		LOGGER.debug("recuperadas " + entities.size() + " entidades");
		return entities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(String property, Object value) {
		List<T> entities = null;
		try {
			LOGGER.debug("GenericDao.list(" + property + "," + value + ")");    
			Criteria criteria = getSession().createCriteria(getEntityClass());
			criteria.add(Restrictions.eq(property, value));
			criteria.addOrder(Order.asc(property).ignoreCase());
			entities = criteria.list();
		} catch (Exception e) {
			LOGGER.error("Error en list: " + e.getMessage());
			entities = new ArrayList<T>();
		}
		LOGGER.debug("recuperadas " + entities.size() + " entidades");
		return entities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(int first, int pageSize, Map<String, Boolean> order, Map<String, Object> filters, DaoFilterType comparationType) {
		LOGGER.debug("GenericDao.list -- first: " + first + " pageSize: " + pageSize);

		List<Long> ids = listIDs(first, pageSize, order, filters, comparationType);
		try {
			final List<T> entities;
			if (!ids.isEmpty()) {
				Criteria criteria = getSession().createCriteria(getEntityClass());
				//criteria.add(Restrictions.in(Projections.id().toString(), ids));
				addCriteriaIn(Projections.id().toString(), ids, criteria);
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

				Set<String> aliasList = new HashSet<String>();
				//addFilters(criteria, filters, aliasList);
				addOrder(criteria, order, aliasList);
				addAlias(criteria, aliasList);

				entities = criteria.list();
			} else {
				entities = new ArrayList<T>();
			}
			LOGGER.debug("recuperadas " + entities.size() + " entidades (" + getEntityClass().getName() + ")");
			return entities;
		} catch (Exception e) {
			LOGGER.error("error en list: " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Long> listIDs(int first, int pageSize, Map<String, Boolean> order, Map<String, Object> filters, DaoFilterType comparationType) {
		try {
			LOGGER.debug("GenericDao.listIDs -- first: " + first + " pageSize: " + pageSize);
			Criteria criteria = getSession().createCriteria(getEntityClass()).setProjection(Projections.id());

			Set<String> aliasList = new HashSet<String>();
			addFilters(criteria, filters, comparationType, aliasList);
			addOrder(criteria, order, aliasList);
			addAlias(criteria, aliasList);

			final List<Long> ids;
			criteria.setFirstResult(first);
			// Si el tamanyo de pagina es menor de 0 se asume que no hay tamanyo maximo.
			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			ids = criteria.list();

			LOGGER.debug("recuperadas " + ids.size() + " ids (" + getEntityClass().getName() + ")");
			return ids;
		} catch (Exception e) {
			LOGGER.error("error en list: " + e.getMessage(), e);
			return null;
		}
	}

	protected void addCriteriaIn(String propertyName, List<?> list, Criteria criteria) {
		Disjunction or = Restrictions.disjunction();
		if (list.size() > 1000) {
			while (list.size() > 1000) {
				List<?> subList = list.subList(0, 1000);
				or.add(Restrictions.in(propertyName, subList));
				list.subList(0, 1000).clear();
			}
		}
		or.add(Restrictions.in(propertyName, list));
		criteria.add(or);
	}

	protected Criteria addFilters(Criteria criteria, Map<String, Object> filters, DaoFilterType comparationType, Set<String> aliasList) {
		try {
			if (filters != null) {
				String filterValue;
				for (String filterProperty : filters.keySet()) {
					filterValue = String.valueOf(filters.get(filterProperty));
					if (filterProperty.indexOf(',') > 0 || filterValue.indexOf('|') > 0) {
						addOrFilters(criteria, Arrays.asList(filterProperty.split(",")), Arrays.asList(filterValue.split("\\|")), comparationType, aliasList);
					} else if (!"".equals(filterProperty)) {
						LOGGER.debug("addFilter");
						criteria.add(getCriterion(filterProperty, filterValue, comparationType, aliasList));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error en addFilters: " + e.getMessage(), e);
		}
		return criteria;
	}

	private Criteria addOrFilters(Criteria criteria, List<String> filterProperties, List<String> filterValues, DaoFilterType comparationType, Set<String> aliasList) {
		try {
			if (filterProperties != null) {
				Disjunction disjunction = Restrictions.disjunction();
				for (String filterProperty : filterProperties) {
					for (String filterValue : filterValues) {
						LOGGER.debug("addOrFilter");
						disjunction.add(getCriterion(filterProperty, filterValue, comparationType, aliasList));
					}
				}
				criteria.add(disjunction);
			}
		} catch (Exception e) {
			LOGGER.error("error en addOrFilters: " + e.getMessage(), e);
		}
		return criteria;
	}

	/**
	 * Funcion que debe sobreescribirse en el caso de querer especificar criterios de busqueda especiales
	 * 
	 * @return criterio de busqueda especial o null en caso de no encontrarlo
	 */
	protected Criterion getEspecialCriterion(String filterProperty, String filterValue, Set<String> aliasList) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Criterion getCriterion(String filterProperty, String filterValue, DaoFilterType comparationType, Set<String> aliasList) {

		// Comprobamos si la propiedad se corresponde con un criterio especial
		Criterion especialCriterion = getEspecialCriterion(filterProperty, filterValue, aliasList);
		if (especialCriterion != null) {
			return especialCriterion;
		}

		Criterion criterion = null;
		try {
			int dotPosition;
			String alias;
			Class filterType;
			String filterColumn;
			if (!"".equals(filterProperty)) {
				String aux = filterProperty;
				dotPosition = aux.indexOf('.');
				filterType = getEntityClass();
				alias = "";
				while (dotPosition > 0) {
					filterType = ObjectUtil.getFieldType(filterType, aux.substring(0, dotPosition));
					alias += "".equals(alias) ? "" : ".";
					alias += aux.substring(0, dotPosition);
					aliasList.add(alias);
					aux = aux.substring(dotPosition + 1);
					dotPosition = aux.indexOf('.');
				}
				filterProperty = filterProperty.replace(alias, alias.replace(".", "_"));
				filterColumn = ObjectUtil.getColumnName(filterType, aux);
				filterColumn = filterType.equals(getEntityClass()) ? "this_." + filterColumn : filterColumn;
				if (ObjectUtil.isId(filterType, aux)) {
					filterType = Id.class;
				} else {
					filterType = ObjectUtil.getFieldType(filterType, aux);
				}
				if (Id.class.equals(filterType)) {
					criterion = Restrictions.eq(filterProperty, Long.parseLong(filterValue));
					LOGGER.debug("criterion: " + filterProperty + " = " + filterValue);
				} else if ("null".equals(filterValue)) {
					criterion = Restrictions.isNull(filterProperty);
					LOGGER.debug("criterion: " + filterProperty + " is null");
				} else if ("not null".equals(filterValue)) {
					criterion = Restrictions.isNotNull(filterProperty);
					LOGGER.debug("criterion: " + filterProperty + " is not null");
				} else if ("empty".equals(filterValue)) {
					criterion = Restrictions.eq(filterProperty, "");
					LOGGER.debug("criterion: " + filterProperty + " is empty");
				} else if ("not empty".equals(filterValue)) {
					criterion = Restrictions.ne(filterProperty, "");
					LOGGER.debug("criterion: " + filterProperty + " is not empty");
				} else if ("list empty".equals(filterValue)) {
					criterion = Restrictions.isEmpty(filterProperty);
					LOGGER.debug("addFilter " + filterProperty + " list is empty");
				} else if ("list not empty".equals(filterValue)) {
					criterion = Restrictions.isNotEmpty(filterProperty);
					LOGGER.debug("criterion: " + filterProperty + " list is not empty");
				} else {
					LOGGER.debug("filterType: " + filterType.getName());
					LOGGER.debug("filterProperty: " + filterProperty);
					LOGGER.debug("filterColumn: " + filterColumn);
					LOGGER.debug("filterValue: " + filterValue);
					switch (comparationType) {
					case STARTWITH:
						filterValue = filterValue + "%";
						break;
					case ENDSWITH:
						filterValue = "%" + filterValue;
						break;
					case EXACT:
						break;
					default:
						filterValue = "%" + filterValue + "%";
						break;
					}
					if (short.class.equals(filterType) || Short.class.equals(filterType) 
							|| int.class.equals(filterType) || Integer.class.equals(filterType) 
							|| long.class.equals(filterType) || Long.class.equals(filterType)) {
						criterion = Restrictions.sqlRestriction(filterColumn + " like '" + filterValue + "'");
					} else if (float.class.equals(filterType) || Float.class.equals(filterType) 
							|| double.class.equals(filterType) || Double.class.equals(filterType) 
							|| BigDecimal.class.equals(filterType)) {
						if (isMySQLDb()) {
							filterProperty = "replace(cast(" + filterColumn + " AS CHAR), '.', ',')";
						} else if (isOracleDb()) {
							filterProperty = "trim(to_char(" + filterColumn + ", '999999999990D00', 'NLS_NUMERIC_CHARACTERS = '',.'''))";
						} else {
							filterProperty = filterColumn;
						}
						criterion = Restrictions.sqlRestriction(filterProperty + " like '" + filterValue + "'");
					} else if (Date.class.equals(filterType)) {
						if (dotPosition < 0) {
							// Solo anyado el criterio de comparacion de fechas cuando no encuentro un punto.
							// En caso contrario, no anyado el criterio, habra que hacerlo a mano en una funcion especifica para ello.
							// El problema es que la funcion sqlRestriction no es hql, y por lo tanto no sabe interpretar de forma correcta "entidad.propiedad".
							if (isMySQLDb()) {
								filterProperty = "date_format(" + filterColumn + ", '%d/%m/%Y %H:%i:%s')";
							} else if (isOracleDb()) {
								filterProperty = "to_char(" + filterColumn + ", 'DD/MM/YYYY HH24:MI:SS')";
							}
							criterion = Restrictions.sqlRestriction(filterProperty + " like '" + filterValue + "'");
						} else {
							// O pongo esto, o tengo que tener en cuenta que esta funcion puede devolver un criterion null.
							criterion = Restrictions.sqlRestriction("1 = 1");
						}
					} else if (Boolean.class.equals(filterType) || boolean.class.equals(filterType)) {
						String fv = filterValue.replaceAll("%", "").toLowerCase();
						if (fv.equals("true") || fv.equals("t") || fv.equals("yes") || fv.equals("y") || fv.equals("si")
								|| fv.equals("s")) {
							criterion = Restrictions.eq(filterProperty, true);
						} else if (fv.equals("false") || fv.equals("f") || fv.equals("no") || fv.equals("n")) {
							criterion = Restrictions.eq(filterProperty, false);
						} else {
							// Esto en oracle falla: criterion = Restrictions.eq(filterProperty, null);
							// O pongo esto, o tengo que tener en cuenta que esta funcion puede devolver un criterion null.
							criterion = Restrictions.sqlRestriction("1 = 1");
						}
					} else {
						if (isOracleDb()) {
							criterion = new IlikeNoAccents(filterProperty, filterValue);
						} else {
							criterion = Restrictions.ilike(filterProperty, filterValue);
						}
					}
					LOGGER.debug("criterion: " + filterProperty + " like '" + filterValue + "'");
				}
			}
		} catch (Exception e) {
			LOGGER.error("error en getCriterion: " + e.getMessage(), e);
			// O pongo esto, o tengo que tener en cuenta que esta funcion puede devolver un criterion null.
			criterion = Restrictions.sqlRestriction("1 = 1");
		}
		return criterion;
	}

	protected Criteria addOrder(Criteria criteria, Map<String, Boolean> order, Set<String> aliasList) {
		try {
			if (order != null && order.size() > 0) {
				Boolean orderASC;
				for (String orderBy : order.keySet()) {
					if (!"".equals(orderBy)) {
						// Si tiene un punto, es una propiedad de una subclase y tendremos que anyadir todos los alias necesarios
						String aux = orderBy;
						int dotPosition = aux.indexOf('.');
						String alias = "";
						while (dotPosition > 0) {
							alias += "".equals(alias) ? "" : ".";
							alias += aux.substring(0, dotPosition);
							aliasList.add(alias);
							aux = aux.substring(dotPosition + 1);
							dotPosition = aux.indexOf('.');
						}

						// Recuperamos la direccion del orden
						orderASC = order.get(orderBy);
						// Sustituimos los puntos del alias por subrayados
						orderBy = orderBy.replace(alias, alias.replace(".", "_"));

						// Ponemos el orden
						if (orderASC) {
							criteria.addOrder(Order.asc(orderBy).ignoreCase());
							LOGGER.debug("addOrder " + orderBy + " ASC");
						} else {
							criteria.addOrder(Order.desc(orderBy).ignoreCase());
							LOGGER.debug("addOrder " + orderBy + " DESC");
						}
					}
				}
				criteria.addOrder(Order.desc("id")); // Para que funcione correctamente el orden cuando se ordena por una columna que puede tener valores nulos, siempre anyadimos com ultimo campo de ordenacion el id
			}
		} catch (Exception e) {
			LOGGER.error("error en addOrder: " + e.getMessage(), e);
		}
		return criteria;
	}

	protected Criteria addAlias(Criteria criteria, Set<String> aliasList) {
		try {
			if (aliasList != null) {
				for (String alias : aliasList) {
					criteria.createAlias(alias, alias.replace(".", "_"));
					LOGGER.debug("addAlias " + alias + " " + alias.replace(".", "_"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("error en addAlias: " + e.getMessage(), e);
		}
		return criteria;
	}

	protected boolean isTestDb() {
		return getDataBaseName().toLowerCase().startsWith("hsql");
	}

	protected boolean isMySQLDb() {
		return getDataBaseName().toLowerCase().startsWith("mysql");
	}

	protected boolean isOracleDb() {
		return getDataBaseName().toLowerCase().startsWith("oracle");
	}

	protected boolean isPostgreSQLDb() {
		return getDataBaseName().toLowerCase().startsWith("postgresql");
	}

	private String getDataBaseName() {
		return getSession().doReturningWork(new ReturningWork<String>() {
			@Override
			public String execute(Connection connection) throws SQLException {
				DatabaseMetaData metaData = connection.getMetaData();
				//LOGGER.debug(metaData.getDatabaseProductName());
				//LOGGER.debug(metaData.getDriverName());
				//LOGGER.debug(metaData.getURL());
				return metaData.getDatabaseProductName();
			}
		});
	}

	public class IlikeNoAccents implements Criterion {

		private static final long serialVersionUID = -1646627372843934692L;

		private final String propertyName;
		private final Object value;

		public IlikeNoAccents(String propertyName, Object value) {
			this.propertyName = propertyName;
			this.value = value;
		}

		@Override
		public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);

			if (columns.length != 1) {
				throw new HibernateException("ilike may only be used with single-column properties");
			}

			return "translate(lower(" + columns[0] + "), '������������������������', 'aaaaaaeeeeiiiiooooouuuucn') like ?";
			//return "replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(lower(" + columns[0] + "),'�','a'), '�', 'a'), '�', 'a'), '�', 'a'), '�', 'a'), '�', 'e'), '�', 'e'), '�', 'e'), '�', 'i'), '�', 'i'), '�', 'i'), '�', 'i'), '�', 'o'), '�', 'o'), '�', 'o'), '�', 'o'), '�', 'o'), '�', 'u'), '�', 'u'), '�', 'u'), '�', 'u'), '�', 'c')  like ?";
		}

		@Override
		public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			String texto = Normalizer.normalize(value.toString().toLowerCase(), Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			return new TypedValue[] { criteriaQuery.getTypedValue( criteria, propertyName, texto) };
		}

	}
}