package br.com.casadocodigo.javacred.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NegociacaoException extends RuntimeException {

	public NegociacaoException(String msg) {
		super(msg);
	}
}
