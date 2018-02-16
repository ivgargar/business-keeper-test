package org.businesskeeper.test.exception;

public class IncorrectInputParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncorrectInputParameterException() {
		super();
	}

	public IncorrectInputParameterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IncorrectInputParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncorrectInputParameterException(String message) {
		super(message);
	}

	public IncorrectInputParameterException(Throwable cause) {
		super(cause);
	}

}
