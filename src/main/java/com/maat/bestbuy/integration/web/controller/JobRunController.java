package com.maat.bestbuy.integration.web.controller;

import com.maat.bestbuy.integration.model.Payload;
import com.maat.bestbuy.integration.model.Response;
import com.maat.bestbuy.integration.service.JobRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import javax.validation.Valid;

@RestController
public class JobRunController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunController.class);

    private JobRunService jobRunService;

    @Autowired
    public JobRunController(JobRunService jobRunService) {
        this.jobRunService = jobRunService;
    }

    @PostMapping(value = "/deployToAnsible/execute", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @CrossOrigin @ResponseBody
    DeferredResult<Response> deployToAnsible(@RequestBody @Valid Payload payload) {
        LOGGER.info("controller(): -> deploy to Ansible => payload: {}", payload);
        Observable<Response> responseObservable = jobRunService.deployToAnsible(payload);
        DeferredResult<Response> result = new DeferredResult<>();
        responseObservable.subscribe(response-> result.setResult(response), e-> result.setErrorResult(e));
        return result;
    }
}