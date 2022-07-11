package com.lftech.sqlapi.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Setter
@ToString
public class ErrorResponse {

    private String id;
    private String error;
    private String message;
    private Integer status; // We'd need it as integer in JSON serialization
    private Collection<FieldError> errors;

    public boolean incomplete() {

        return message == null || status == null;
    }
}
