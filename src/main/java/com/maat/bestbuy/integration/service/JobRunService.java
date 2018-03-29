package com.maat.bestbuy.integration.service;

import com.maat.bestbuy.integration.dao.JobRunDAO;
import com.maat.bestbuy.integration.model.Payload;
import com.maat.bestbuy.integration.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service("jobRunService")
public class JobRunService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunService.class);

    private JobRunDAO jobRunDAO;

    @Autowired
    public JobRunService(JobRunDAO jobRunDAO) {
        this.jobRunDAO = jobRunDAO;
    }

    public Observable<Response> deployToAnsible(Payload payload) {
        Response response = jobRunDAO.deployToAnsible(payload);
        return Observable.just(response);
    }
}
