package com.spring.cloud.ansiblePython.service;

import com.spring.cloud.ansiblePython.dao.DAO;
import com.spring.cloud.ansiblePython.model.Payload;
import com.spring.cloud.ansiblePython.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

@org.springframework.stereotype.Service("jobRunService")
public class Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private DAO DAO;

    @Autowired
    public Service(DAO DAO) {
        this.DAO = DAO;
    }

    public Observable<Response> deployToAnsible(Payload payload) {
        Response response = DAO.deployToAnsible(payload);
        return Observable.just(response);
    }
}
