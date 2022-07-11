package com.lftech.sqlapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

// https://www.naturalprogrammer.com/blog/17857/spring-boot-webflux-reactive-rest-web-services-validation
@Component
@Slf4j
public class CustomErrorAttributes<T extends Throwable> extends DefaultErrorAttributes {

    private ErrorResponseComposer<T> errorResponseComposer;

    public CustomErrorAttributes(ErrorResponseComposer<T> errorResponseComposer) {

        this.errorResponseComposer = errorResponseComposer;
        log.info("Created");
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest request,
                                                  ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        addErrorDetails(errorAttributes, request);
        return errorAttributes;
    }

    @SuppressWarnings("unchecked")
    protected void addErrorDetails(
            Map<String, Object> errorAttributes, WebRequest request) {
        Throwable ex = getError(request);

        errorResponseComposer.compose((T) ex).ifPresent(errorResponse -> {
            if (errorResponse.getMessage() != null)
                errorAttributes.put("message", errorResponse.getMessage());
            Integer status = errorResponse.getStatus();
            if (status != null) {
                errorAttributes.put("status", status);
                errorAttributes.put("error", errorResponse.getError());
            }
            if (errorResponse.getErrors() != null)
                errorAttributes.put("errors", errorResponse.getErrors());
        });
    }
}
