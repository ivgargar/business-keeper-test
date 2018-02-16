package org.businesskeeper.test.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApiErrorResponse {
	private List<ApiError> errors;
	
    public ApiErrorResponse() {
    }

    public ApiErrorResponse(List<ApiError> errors) {
        this.errors = errors;
    }

    public ApiErrorResponse(ApiError error) {
        this(Collections.singletonList(error));
    }

    public ApiErrorResponse(ApiError ... errors) {
        this(Arrays.asList(errors));
    }

	public List<ApiError> getErrors() {
		return errors;
	}

	public void setErrors(List<ApiError> errors) {
		this.errors = errors;
	}
	
}
