package com.maat.bestbuy.integration.exception;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.Arrays;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -4591939994371526941L;
    private static final String MESSAGE_CODE = "runtime.exception";
    private final Object[] params;

    public BadRequestException(String messageKey, Object[] params) {
        super(StringUtils.isBlank(messageKey) ? MESSAGE_CODE : messageKey);
        this.params = params == null ? null : params.clone();
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.params = null;
    }

    public BadRequestException(String message) {
        super(message);
        this.params = null;
    }

    public BadRequestException(String messageKey, Object[] params, Throwable cause) {
        super(StringUtils.isBlank(messageKey) ? MESSAGE_CODE : messageKey, cause);
        this.params = params == null ? null : params.clone();
    }

    public Object[] getParams() {
        if (params != null) {
            return Arrays.copyOf(params, params.length);
        } else {
            return new Object[0];
        }
    }

}
