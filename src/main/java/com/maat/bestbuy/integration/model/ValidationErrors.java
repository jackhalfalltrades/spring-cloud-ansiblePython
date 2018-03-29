package com.maat.bestbuy.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ValidationErrors {

    private List<RequestPropertyError> requestErrors = new ArrayList<>();

    public void addRequestError(String path, String message) {
        RequestPropertyError error = new RequestPropertyError(path, message);
        requestErrors.add(error);
    }

}
