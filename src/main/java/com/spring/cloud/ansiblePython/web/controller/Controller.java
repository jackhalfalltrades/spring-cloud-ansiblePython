package com.spring.cloud.ansiblePython.web.controller;

import com.spring.cloud.ansiblePython.model.Payload;
import com.spring.cloud.ansiblePython.model.Response;
import com.spring.cloud.ansiblePython.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import javax.validation.Valid;

@RestController
public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private Service service;

    @Autowired
    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(value = "/deployToAnsible/execute", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @CrossOrigin
    @ResponseBody
    DeferredResult<Response> deployToAnsible(@RequestBody @Valid Payload payload) {
        LOGGER.info("controller(): -> deploy to Ansible => payload: {}", payload);
        Observable<Response> responseObservable = service.deployToAnsible(payload);
        DeferredResult<Response> result = new DeferredResult<>();
        responseObservable.subscribe(response -> result.setResult(response), e -> result.setErrorResult(e));
        return result;
    }
}