package com.xalo.java8.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EjemplosLambda {

	private static final Logger LOGGER = LogManager.getLogger(EjemplosLambda.class);
	
	 public static void main(String[] args) {
	        List<String> names = Arrays.asList("abc1", "zzz", "chicken");
	        // sort alphabetically
	        Collections.sort(names);
	        System.out.println("names sorted alphabetically  >>");
	        System.out.println(names);
	        System.out.println();

	        // using anonymous classes
	        Collections.sort(names, new Comparator<String>() {
	            @Override
	            public int compare(String o1, String o2) {
	                return o1.length() - o2.length();
	            }
	        });
	        LOGGER.info("names sorted by length  >>");
	        LOGGER.info(names);

	        /**
	         * Using lambda
	         * Things to show >>
	         * 1. return statement
	         * 2. Without return statement
	         * 3. Multiple lines
	         * 4. Type inference
	         */

	        Collections.sort(names, (String first, String second) -> second.length() - first.length());
	        LOGGER.info("names sorted by length(reversed)  >>");
	        LOGGER.info(names);
	    }
}