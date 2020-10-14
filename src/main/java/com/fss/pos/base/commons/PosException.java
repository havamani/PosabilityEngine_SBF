package com.fss.pos.base.commons;

import com.fss.pos.base.commons.constants.Constants;

/**
 * A subclass of {@link Exception} thrown when checked exceptions are caught.
 * 
 * @author Priyan
 *
 */
public class PosException extends Exception {

	private static final long serialVersionUID = 1L;

	private String error;

	@SuppressWarnings("unused")
	private PosException() {

	}

	public PosException(String error) {
		this.error = error;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}

	@Override
	public String getMessage() {
		return (error == null || error.isEmpty()) ? Constants.ERR_SYSTEM_ERROR
				: error;
	}

}
