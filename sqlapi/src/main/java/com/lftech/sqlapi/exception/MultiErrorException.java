package com.lftech.sqlapi.exception;

import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiErrorException extends RuntimeException {
    private static final long serialVersionUID = 6020532846519363456L;
    private List<FieldError> errors = new ArrayList<>(10);
    private HttpStatus status;
    private Class<?>[] validationGroups;

    public MultiErrorException() {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
        this.validationGroups = new Class<?>[0];
    }

    public String getMessage() {
        return this.errors.isEmpty() ? null : (this.errors.get(0)).getMessage();
    }

    public MultiErrorException httpStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public MultiErrorException validationGroups(Class<?>... groups) {
        this.validationGroups = groups;
        return this;
    }

    public MultiErrorException validateField(String fieldName, boolean valid, String messageKey, Object... args) {
        if (!valid) {
            this.errors.add(new FieldError(fieldName, messageKey, messageKey));
        }

        return this;
    }

    public MultiErrorException validate(boolean valid, String messageKey, Object... args) {
        return this.validateField(null, valid, messageKey, args);
    }

    public <T> MultiErrorException validateBean(String beanName, T bean) {
        Set<? extends ConstraintViolation<T>> constraintViolations = ExUtils.validator().validate(bean, this.validationGroups);
        this.addErrors(constraintViolations, beanName);
        return this;
    }

    public void go() {
        if (!this.errors.isEmpty()) {
            throw this;
        }
    }

    private void addErrors(Set<? extends ConstraintViolation<?>> constraintViolations, String objectName) {
        this.errors.addAll(constraintViolations.stream()
            .map((constraintViolation) ->
                new FieldError(objectName + "." + constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessageTemplate(), constraintViolation.getMessage()))
            .collect(Collectors.toList()));
    }

    public List<FieldError> getErrors() {
        return this.errors;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}

