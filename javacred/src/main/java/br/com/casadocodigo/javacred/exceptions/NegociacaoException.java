package br.com.casadocodigo.javacred.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NegociacaoException extends RuntimeException {

	public NegociacaoException(String msg) {
		super(msg);
	}
}
