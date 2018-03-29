package com.maat.bestbuy.integration.exception.handler;

import com.maat.bestbuy.integration.exception.AuthorizationException;
import com.maat.bestbuy.integration.exception.BadRequestException;
import com.maat.bestbuy.integration.exception.InternalServerErrorException;
import com.maat.bestbuy.integration.exception.ResourceNotFoundException;
import com.maat.bestbuy.integration.model.ErrorInfo;
import com.maat.bestbuy.integration.model.ValidationErrors;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler implements MessageSourceAware {

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final String RUNTIME_EXCEPTION = "runtime.exception";

    private static final String HYSTRIX_TIMEOUT_EXCEPTION = "timeout.exception";

    private static final String RESOURCE_NOT_FOUND = "resource.not.found";

    @ExceptionHandler(HystrixTimeoutException.class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    @ResponseBody
    public ErrorInfo circuitException(HttpServletRequest req, HystrixTimeoutException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Hystrix Exception] " + ex.getMessage()), ex);
        String errorMessage = messageSource.getMessage(HYSTRIX_TIMEOUT_EXCEPTION, new Object[]{ex.getMessage()}, LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.REQUEST_TIMEOUT.toString());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo timeoutException(HttpServletRequest req, Exception ex) {
        LOGGER.error("[Internal ESP Server Error] " + ex.getMessage());
        LOGGER.error(ExceptionUtils.getFullStackTrace(ex));

        String errorMessage = messageSource
                .getMessage(RUNTIME_EXCEPTION, new Object[] { ex.getMessage() }, LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo generalException(HttpServletRequest req, Exception ex) {
        LOGGER.error(ExceptionUtils.getFullStackTrace(ex));

        String errorMessage = messageSource.getMessage(RUNTIME_EXCEPTION, new Object[] { ex.getClass().getName() },
                LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo processBadRequestError(HttpServletRequest req, BadRequestException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Bad Request Exception] " + ex.getMessage()), ex);

        String errorMessage = messageSource.getMessage("badrequest.exception", new Object[] {ex.getMessage(), ex.getParams()}, LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.BAD_REQUEST.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo processRequestNotReadableError(HttpServletRequest req, HttpMessageNotReadableException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Http Request Not Readable Exception] " + ex.getMessage()), ex);

        String errorMessage = messageSource.getMessage("requestnotreadable.exception", new Object[] { ex.getMessage() },
                LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.BAD_REQUEST.toString());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo processResourceNotFoundError(HttpServletRequest req, ResourceNotFoundException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Resource Not Found Exception] " + ex.getMessage()), ex);
        String errorMessage = messageSource.getMessage(RESOURCE_NOT_FOUND, new Object[] { ex.getClass().getName(), ex.getLocalizedMessage(), ex.getParams() }, LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.NOT_FOUND.toString());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrors processValidationError(BindException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Validation Bind Exception] " + ex.getMessage()), ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrors processValidationError(MethodArgumentNotValidException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Validation Error] " + ex.getMessage()), ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorInfo authorizationException(HttpServletRequest req, AuthorizationException ex) {
        LOGGER.error(StringEscapeUtils.escapeJava("[Authorization Error] " + ex.getMessage()), ex);
        String errorMessage = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, errorMessage, HttpStatus.UNAUTHORIZED.toString());
    }

    private ValidationErrors processFieldErrors(List<FieldError> fieldErrors) {
        ValidationErrors dto = new ValidationErrors();
        for (FieldError fieldError : fieldErrors) {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            dto.addRequestError(fieldError.getField(), localizedErrorMessage);
        }

        return dto;
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        String localizedErrorMessage = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
        // If a message was not found, return the most accurate field error code
        // instead.
        // You can remove this check if you prefer to get the default error
        // message.
        if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }
        return localizedErrorMessage;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
