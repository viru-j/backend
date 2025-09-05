package com.ub19.shared.model.error;

import org.springframework.http.HttpStatus;

/**
 * Runtime exception carrying an {@link ApiError} and HTTP status.
 */
public class ApiException extends RuntimeException {

    private final ApiError error;
    private final HttpStatus status;

    public ApiException(HttpStatus status, ApiError error) {
        super(error.message());
        this.status = status;
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

