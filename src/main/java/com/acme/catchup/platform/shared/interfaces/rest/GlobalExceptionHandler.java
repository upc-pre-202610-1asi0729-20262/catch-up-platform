package com.acme.catchup.platform.shared.interfaces.rest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * @summary
 * This class handles exceptions thrown by REST controllers.
 * It includes support for localized error messages.
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles MethodArgumentNotValidException.
     * @param exception The {@link MethodArgumentNotValidException} exception to handle
     * @param locale The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(MethodArgumentNotValidException exception, Locale locale) {
        String prefix = messageSource.getMessage("errors.found", null, locale);
        String fields = exception.getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, locale))
                .collect(Collectors.joining(", "));
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                prefix + " " + fields
        );
    }

    /**
     * Handles IllegalArgumentException.
     * @param exception The {@link IllegalArgumentException} exception to handle
     * @param locale The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(IllegalArgumentException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.found";
        String message = Objects.requireNonNullElse(messageSource.getMessage(messageKey, null, messageKey, locale), messageKey);
        return ErrorResponse.create(exception, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

}