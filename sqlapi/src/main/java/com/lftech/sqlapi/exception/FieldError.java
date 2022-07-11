package com.lftech.sqlapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@ToString
public class FieldError implements Serializable {

    private static final long serialVersionUID = 8983662083082503110L;
    // Name of the field. Null in case of a form level error.
    private final String field;

    // Error code. Typically the I18n message-code.
    private final String code;

    // Error message
    private final String message;

    /**
     * Converts a set of ConstraintViolations
     * to a list of FieldErrors
     */
    public static List<FieldError> getErrors(
            Set<ConstraintViolation<?>> constraintViolations) {

        return constraintViolations.stream()
                .map(FieldError::of).collect(Collectors.toList());
    }


    /**
     * Converts a ConstraintViolation
     * to a FieldError
     */
    private static FieldError of(ConstraintViolation<?> constraintViolation) {

        // Get the field name by removing the first part of the propertyPath.
        // (The first part would be the service method name)
//            String field = StringUtils.substringAfter(
//                    constraintViolation.getPropertyPath().toString(), ".");

        String field = null;
        return new FieldError(field,
                constraintViolation.getMessageTemplate(),
                constraintViolation.getMessage());
    }

    public static List<FieldError> getErrors(WebExchangeBindException ex) {

        List<FieldError> errors = ex.getFieldErrors().stream()
                .map(FieldError::of).collect(Collectors.toList());

        errors.addAll(ex.getGlobalErrors().stream()
                .map(FieldError::of).collect(Collectors.toSet()));

        return errors;
    }

    private static FieldError of(org.springframework.validation.FieldError fieldError) {

        return new FieldError(fieldError.getObjectName() + "." + fieldError.getField(),
                fieldError.getCode(), fieldError.getDefaultMessage());
    }

    public static FieldError of(ObjectError error) {

        return new FieldError(error.getObjectName(),
                error.getCode(), error.getDefaultMessage());
    }
}
