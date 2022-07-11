package com.lftech.sqlapi.exception.handler;


import com.lftech.sqlapi.exception.ErrorResponse;
import com.lftech.sqlapi.exception.FieldError;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.UUID;

public abstract class AbstractExceptionHandler<T extends Throwable> {
    private final Class<?> exceptionClass;

    public AbstractExceptionHandler(Class<?> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public Class<?> getExceptionClass() {
        return this.exceptionClass;
    }

    protected String getMessage(T ex) {
        return ex.getMessage();
    }

    protected HttpStatus getStatus(T ex) {
        return null;
    }

    protected Collection<FieldError> getErrors(T ex) {
        return null;
    }

    public ErrorResponse getErrorResponse(T ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setId(UUID.randomUUID().toString());
        errorResponse.setMessage(this.getMessage(ex));
        HttpStatus status = this.getStatus(ex);
        if (status != null) {
            errorResponse.setStatus(status.value());
            errorResponse.setError(status.getReasonPhrase());
        }

        errorResponse.setErrors(this.getErrors(ex));
        return errorResponse;
    }
}
