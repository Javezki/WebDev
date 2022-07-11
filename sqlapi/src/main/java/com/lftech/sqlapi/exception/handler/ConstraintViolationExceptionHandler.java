package com.lftech.sqlapi.exception.handler;

import com.lftech.sqlapi.exception.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.Collection;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class ConstraintViolationExceptionHandler extends AbstractValidationExceptionHandler<ConstraintViolationException> {

	public ConstraintViolationExceptionHandler() {

		super(ConstraintViolationException.class);
		log.info("Created");
	}

	@Override
	public Collection<FieldError> getErrors(ConstraintViolationException ex) {
		return FieldError.getErrors(ex.getConstraintViolations());
	}

}
