package com.lftech.sqlapi.exception.handler;

import com.lftech.sqlapi.exception.FieldError;
import com.lftech.sqlapi.exception.MultiErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class MultiErrorExceptionHandler extends AbstractExceptionHandler<MultiErrorException> {

	public MultiErrorExceptionHandler() {

		super(MultiErrorException.class);
		log.info("Created");
	}

	@Override
	public String getMessage(MultiErrorException ex) {
		return ex.getMessage();
	}

	@Override
	public HttpStatus getStatus(MultiErrorException ex) {
		return ex.getStatus();
	}

	@Override
	public Collection<FieldError> getErrors(MultiErrorException ex) {
		return ex.getErrors();
	}
}
