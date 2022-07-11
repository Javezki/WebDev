package com.lftech.sqlapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.function.Supplier;

@Slf4j
@Component
public class ExUtils {

    private static MessageSource messageSource;
    private static LocalValidatorFactoryBean validator;

    private static final Validator DEFAULT_VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static final MultiErrorException NOT_FOUND_EXCEPTION = new MultiErrorException();

    /**
     * Constructor
     */
    public ExUtils(MessageSource messageSource,
                   LocalValidatorFactoryBean validator) {

        ExUtils.messageSource = messageSource;
        ExUtils.validator = validator;

        log.info("Created");
    }


    @PostConstruct
    public void postConstruct() {

        NOT_FOUND_EXCEPTION
                .httpStatus(HttpStatus.NOT_FOUND)
                .validate(false, "未找到");

        log.info("NOT_FOUND_EXCEPTION built");
    }


    /**
     * Gets a message from messages.properties
     */
    public static String getMessage(String messageKey, Object... args) {

        if (messageSource == null) // ApplicationContext unavailable, probably unit test going on
            return messageKey;

        // http://stackoverflow.com/questions/10792551/how-to-obtain-a-current-user-locale-from-spring-without-passing-it-as-a-paramete
        return messageSource.getMessage(messageKey, args,
                LocaleContextHolder.getLocale());
    }


    /**
     * Creates a MultiErrorException out of the given parameters
     */
    public static MultiErrorException validate(
            boolean valid, String messageKey, Object... args) {

        return validateField(null, valid, messageKey, args);
    }


    /**
     * Creates a MultiErrorException out of the given parameters
     */
    public static MultiErrorException validateField(
            String fieldName, boolean valid, String messageKey, Object... args) {

        return new MultiErrorException().validateField(fieldName, valid, messageKey, args);
    }


    /**
     * Creates a MultiErrorException out of the constraint violations in the given bean
     */
    public static <T> MultiErrorException validateBean(String beanName, T bean, Class<?>... validationGroups) {

        return new MultiErrorException()
                .validationGroups(validationGroups)
                .validateBean(beanName, bean);
    }


    /**
     * Throws 404 Error is the entity isn't found
     */
    public static <T> void ensureFound(T entity) {

        validate(entity != null,
                "未找到")
                .httpStatus(HttpStatus.NOT_FOUND).go();
    }


    /**
     * Supplys a 404 exception
     */
    public static Supplier<MultiErrorException> notFoundSupplier() {

        return () -> NOT_FOUND_EXCEPTION;
    }

    private static Throwable getRootException(Throwable ex) {

        if (ex == null)
            return null;

        while(ex.getCause() != null)
            ex = ex.getCause();

        return ex;
    }


    public static Validator validator() {
        return validator == null ? // e.g. in unit tests
                DEFAULT_VALIDATOR : validator;
    }
}

