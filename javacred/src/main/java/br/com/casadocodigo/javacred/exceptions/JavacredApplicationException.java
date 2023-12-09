package br.com.casadocodigo.javacred.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException
public class JavacredApplicationException extends JavacredException {

	private static final long serialVersionUID = -5921359509337000329L;

	public JavacredApplicationException(String msg) {
		super(msg);
	}
}
