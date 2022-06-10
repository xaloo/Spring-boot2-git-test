package com.xalo.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Formula;

public final class ObjectUtil {

	private static final Logger LOGGER = LogManager.getLogger(ObjectUtil.class);

	private ObjectUtil() {
	}

	public static void emptyStringsToNull( Object object ) {
		if (object != null) {
			emptyStringsToNull(object, object.getClass());
		}
	}

	private static void emptyStringsToNull( Object object, Class<?> clase ) {
		try {
			if (clase.getSuperclass() != null) {
				emptyStringsToNull(object, clase.getSuperclass());
			}
			String value;
			for ( Field field : clase.getDeclaredFields() ) {
				field.setAccessible( true );

				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				if (field.getType().equals(String.class)) {
					value = (String)field.get(object);
					if (StringUtils.isEmpty(value)) {
						field.set(object, null);
					}
				} else if (field.get( object ) instanceof List<?> && !((List<?>)field.get( object )).isEmpty() && 
						(((List<?>)field.get( object )).get(0).getClass().isAnnotationPresent(Entity.class))) { // Listas de entidades
					for (Object o : ((Collection<?>) field.get( object ))) {
						emptyStringsToNull(o);
					}
				} else if (field.isAnnotationPresent(ManyToOne.class)) { // Entidades anotadas con ManyToOne y CascadeType.ALL
					ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
					if (Arrays.asList(manyToOne.cascade()).contains(CascadeType.ALL)) {
						emptyStringsToNull(field.get(object));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en emptyStringsToNull: " + e.getMessage(), e);
		}
	}

	public static void toUppercase( Object object ) {
		if (object != null) {
			toUppercase(object, object.getClass());
		}
	}	
	private static void toUppercase( Object object, Class<?> clase ) {
		try {
			if (clase.getSuperclass() != null) {
				toUppercase(object, clase.getSuperclass());
			}
			String value;
			for ( Field field : clase.getDeclaredFields() ) {
				field.setAccessible( true );

				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				/*if (field.get( object ) instanceof List<?> &&
						!((List<?>)field.get( object )).isEmpty() && 
						!(((List<?>)field.get( object )).get(0) instanceof String)) { // Listas de entidades, no de strings
					int i = 0;
					Field fieldId;
					for (Object o : ((Collection<?>) field.get( object ))) {
						try {
							// Si tiene campo id lo utilizo como identificador de la fila
							fieldId = o.getClass().getDeclaredField("id");
							fieldId.setAccessible(true);
							map.putAll(toMap(prefix + field.getName() + "_" + fieldId.get(o) + ".", o));
						} catch (NoSuchFieldException e) {
							// Si no tiene campo id utilizo el indice como identificador de la fila
							map.putAll(toMap(prefix + field.getName() + "_" + i + ".", o));
						}
						i++;
					}
				} else*/ if (field.getType().getName().startsWith("com.palluc")) {
					//LOGGER.debug("toUppercase "+ field.getName());
					toUppercase(field.get( object ));
				} else if (field.getType().equals(String.class)) {
					value = (String)field.get(object);
					if (value != null) {
						field.set(object, value.toUpperCase());
						//LOGGER.debug("toUppercase "+ field.getName() + ": " + value.toUpperCase());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en toUppercase: " + e.getMessage(), e);
		}
	}

	public static Serializable getId(Object o) {
		Serializable id = -1;
		try {
			if (o != null) {
				for(Field field  : o.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(Id.class)) {
						field.setAccessible(true);
						id = (Serializable) field.get(o);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en getId: " + e.getMessage(), e);
		}
		return id;
	}

	public static void setId(Object o, Serializable id) {
		try {
			if (o != null) {
				for(Field field  : o.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(Id.class)) {
						field.setAccessible(true);
						field.set(o, id);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en setId: " + e.getMessage(), e);
		}
	}

	public static boolean isId(Class<?> c, String property) {
		boolean res = false;
		try {
			Field field = c.getDeclaredField(property);
			res = field.isAnnotationPresent(Id.class);
		} catch (Exception e) {
			LOGGER.error("Error en isId: " + e.getMessage(), e);
		}
		return res;
	}

	public static Class<?> getFieldType(Class<?> c, String property) {
		Class<?> type = null;
		try {
			Field field = c.getDeclaredField(property);
			type = field.getType();
			if (Collection.class.isAssignableFrom(field.getType())) {
				ParameterizedType listType = (ParameterizedType) field.getGenericType();
				type = (Class<?>) listType.getActualTypeArguments()[0];
			}
		} catch (Exception e) {
			LOGGER.error("Error en getFieldType: " + e.getMessage(), e);
		}
		return type;
	}

	public static String getColumnName(Class<?> c, String property) {
		String columnName = property;
		try {
			Field field = c.getDeclaredField(property);
			if (field.isAnnotationPresent(Column.class)) {
				Column a = field.getAnnotation(Column.class);
				columnName = a.name();
				if ("".equals(columnName)) {
					columnName = property;
				}
			} else if (field.isAnnotationPresent(Formula.class)) {
				Formula f = field.getAnnotation(Formula.class);
				columnName = f.value();
			}
		} catch (Exception e) {
			LOGGER.debug("No se ha podido obtener el nombre de columna de la propiedad: " + property);
		}
		return columnName;
	}

	/**
	 * Funcion que ajusta el tamanyo de los campos de tipo cadena (String) de un objeto
	 * El tamanyo de la propiedad tiene que estar definido con un anotacion del tipo @Column(length=10)
	 * @param object objeto
	 */	
	public static void truncateEntityLength(Object object) {
		truncateEntityLength(object, false);
	}

	/**
	 * Funcion que ajusta el tamanyo de los campos de tipo cadena (String) de un objeto
	 * El tamanyo de la propiedad tiene que estar definido con un anotacion del tipo @Column(length=10)
	 * @param object objeto
	 * @param recursive procesar de forma recursiva las propiedades
	 * Si tiene propiedades de tipo list o set, no se procesan los elementos de la lista
	 */	
	@SuppressWarnings("rawtypes")
	public static void truncateEntityLength(Object object, boolean recursive) {
		int fieldMaxLength;
		Class fieldType;
		Object fieldValue;
		try {
			Class<?> objClass = object.getClass();
			if (objClass.isAnnotationPresent(Entity.class)) {
				//LOGGER.debug("ObjectUtil.isValid - objClass: "+objClass.getName());
				for (Field field : objClass.getDeclaredFields()) {
					field.setAccessible( true );
					fieldValue = field.get(object);
					if (fieldValue != null) {
						fieldType = field.getType();
						if (String.class.equals(fieldType)) { //Cadenas
							//Validamos el tamanyo del campo (En la entidad debe de estar la anotacion @Column(length=10)
							if (field.isAnnotationPresent(Column.class)) {
								fieldMaxLength = field.getAnnotation(Column.class).length();							
								if ( ((String)fieldValue).length() > fieldMaxLength ) {
									LOGGER.debug("truncate invalid length: field: " + field.getName() + " length: " + ((String)fieldValue).length() + " maxlength: " + fieldMaxLength + " value: " + fieldValue);
									field.set(object, ((String)fieldValue).substring(0, fieldMaxLength));
								}
							} else if (recursive) {
								truncateEntityLength(fieldValue, true);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en truncateEntityLength: " + e.getMessage(), e);
		}
	}
}