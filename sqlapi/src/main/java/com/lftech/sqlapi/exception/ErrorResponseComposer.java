package com.lftech.sqlapi.exception;

import com.lftech.sqlapi.exception.handler.AbstractExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ErrorResponseComposer<T extends Throwable> {

    private final Map<Class<?>, AbstractExceptionHandler<T>> handlers;

    @Autowired
    public ErrorResponseComposer(List<AbstractExceptionHandler<T>> handlers) {

        this.handlers = handlers.stream().collect(
                Collectors.toMap(AbstractExceptionHandler::getExceptionClass,
                        Function.identity(), (handler1, handler2) ->
                                AnnotationAwareOrderComparator
                                        .INSTANCE.compare(handler1, handler2) < 0 ?
                                        handler1 : handler2
                ));
        log.info("Created");
    }

    /**
     * Given an exception, finds a handler for
     * building the response and uses that to build and return the response
     */
    @SuppressWarnings("unchecked")
    public Optional<ErrorResponse> compose(T ex) {

        AbstractExceptionHandler<T> handler = null;

        // find a handler for the exception
        // if no handler is found,
        // loop into for its cause (ex.getCause())

        while (ex != null) {

            handler = handlers.get(ex.getClass());

            if (handler != null) // found a handler
                break;

            ex = (T) ex.getCause();
        }

        if (handler != null) // a handler is found
            return Optional.of(handler.getErrorResponse(ex));

        return Optional.empty();
    }
}
