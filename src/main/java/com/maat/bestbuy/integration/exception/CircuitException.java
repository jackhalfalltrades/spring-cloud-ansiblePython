package com.maat.bestbuy.integration.exception;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

public class CircuitException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3591931194371986941L;
    private static final String MESSAGE_CODE = "runtime.exception";
    private final Object[] params;

    public CircuitException(String messageKey, Object[] params) {
        super(StringUtils.isBlank(messageKey) ? MESSAGE_CODE : messageKey);
        this.params = params == null ? null : params.clone();
    }

    public CircuitException(String message, Throwable cause) {
        super(message, cause);
        this.params = null;
    }

    public CircuitException(String message) {
        super(message);
        this.params = null;
    }

    public CircuitException(String messageKey, Object[] params, Throwable cause) {
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