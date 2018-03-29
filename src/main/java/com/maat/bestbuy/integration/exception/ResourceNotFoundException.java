package com.maat.bestbuy.integration.exception;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.Arrays;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -54623194351074231L;

    private Object[] params;

    public ResourceNotFoundException(String messageKey, Object[] params) {
        super(StringUtils.isBlank(messageKey) ? "resource.not.found" : messageKey);
        this.params = params == null? null : params.clone();
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.params = null;
    }

    public ResourceNotFoundException(String message) {
        super (message);
        this.params = null;
    }

    public Object[] getParams() {
        if(params != null) {
            return Arrays.copyOf(params, params.length);
        } else {
            return new Object[0];
        }
    }
}

