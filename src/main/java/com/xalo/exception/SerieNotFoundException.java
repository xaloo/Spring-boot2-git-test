package com.xalo.exception;

public class SerieNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -442915072546611318L;

	public SerieNotFoundException(Long id) {
		super("No se ha encontrado la serie " + id);
	}
}