package com.xalo.java8.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EjemplosLambda {

	private static final Logger LOGGER = LogManager.getLogger(EjemplosLambda.class);

	public static List<String> ordenar() {
		List<String> lista = new ArrayList<>();
		lista.add("c");
		lista.add("abc");
		lista.add("b");
		Collections.sort(lista);
		return lista;
	}
	
	public static void main (String[] args) {
		List<String> lista = ordenar();
		for (String string: lista) {
			LOGGER.info(string);
		}
	}
}