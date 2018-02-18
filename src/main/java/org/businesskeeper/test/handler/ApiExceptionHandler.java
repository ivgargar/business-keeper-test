package org.businesskeeper.test.handler;

import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ApiError;
import org.businesskeeper.test.model.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for IncorrectInputParameterException.
 * @author ivgargar
 *
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IncorrectInputParameterException.class)
    protected ResponseEntity<Object> handleIncorrectInputParameterException(RuntimeException ex, WebRequest request) {
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST.toString(), "Incorrect Input Parameter", ex.getMessage()));
    }

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(new ApiErrorResponse(apiError), HttpStatus.BAD_REQUEST);
	}
}
