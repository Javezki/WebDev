package com.lftech.sqlapi.exception.handler;

import com.lftech.sqlapi.exception.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collection;

@Component
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class WebExchangeBindExceptionHandler extends AbstractValidationExceptionHandler<WebExchangeBindException> {

    public WebExchangeBindExceptionHandler() {

        super(WebExchangeBindException.class);
        log.info("Created");
    }

    @Override
    public Collection<FieldError> getErrors(WebExchangeBindException ex) {
        return FieldError.getErrors(ex);
    }
}
