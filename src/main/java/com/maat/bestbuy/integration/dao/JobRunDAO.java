package com.maat.bestbuy.integration.dao;

import com.maat.bestbuy.integration.exception.BadRequestException;
import com.maat.bestbuy.integration.exception.MAATRuntimeException;
import com.maat.bestbuy.integration.exception.ResourceNotFoundException;
import com.maat.bestbuy.integration.model.Job;
import com.maat.bestbuy.integration.model.Payload;
import com.maat.bestbuy.integration.model.Response;
import com.maat.bestbuy.integration.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository("jobRunDao")
public class JobRunDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunDAO.class);

    private JobRepository jobRepository;

    @Value("${ansible.module}")
    private String ansibleAPI;

    @Value("${ansible.path}")
    private String ansibleRoot;

    private String status = "";
    private String successfullHostNames = "";
    private String failedHostNames = "";
    private boolean found = false;

    @Autowired
    public JobRunDAO(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Response deployToAnsible(Payload payload) {
        try {
            if (payload.getJobID() != null) {
                Job job = updateJobStatus(queryJobDetails(payload.getJobID()), "Submitted", payload.getUserID());
                String[] args = new String[4];
                args[0] = ansibleAPI;
                args[1] = ansibleRoot;
                args[2] = new String(job.getParam()); //job arguments
                args[3] = job.getJobType(); // task name
                LOGGER.info("deployToAnsible(): -> deploy to ansible command initiated for job {}", payload.getJobID());
                Runtime ansible = Runtime.getRuntime();
                Process process = ansible.exec(args);
                ansibleCallback(process).stream()
                        .map(e -> e.toLowerCase())
                        .forEach(e -> {
                            if (found) {
                                if (e.contains(":")) {
                                    if (e.contains("failed=0")) {
                                        successfullHostNames = successfullHostNames + e.split(":")[0];
                                    } else {
                                        failedHostNames = failedHostNames + e.split(":")[0];
                                    }
                                }
                            }
                            if (e.contains("play recap"))
                                found = true;
                        });
                if (successfullHostNames != "" && successfullHostNames != null)
                    status = status + "Task: " + job.getJobType() + " completed successfully on {" + successfullHostNames + "}. ";
                if (failedHostNames != "" && failedHostNames != null) {
                    status = status + "Task: " + job.getJobType() + " failed on {" + failedHostNames + "} Please check logs for details. ";
                    updateJobStatus(job, "Failed", payload.getUserID());
                } else {
                    updateJobStatus(job, "Completed", payload.getUserID());
                }
                return Response.builder().jobID(payload.getJobID()).status(status).build();
            }
            throw new BadRequestException("JobID is blank in payload");
        } catch (IOException e) {
            throw new MAATRuntimeException(e.getMessage(), new Object[]{e.getClass().getName(), e.getCause(), e});
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage(), new Object[]{e.getClass().getName(), e.getCause(), e});
        }
    }

    private Job queryJobDetails(String jobID) throws ResourceNotFoundException {
        try {
            Optional<Job> job = jobRepository.findById(jobID);
            if (job.isPresent()) {
                return job.get();
            }
            throw new ResourceNotFoundException("No job found for ID: " + jobID);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), new Object[]{e.getClass().getName(), e});
        }
    }

    private Job updateJobStatus(Job job, String status, String userID) {
        job.setJobStatus(status);
        job.setSubmittedBy(userID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        job.setSubmittedTimeStamp(new Date());
        return jobRepository.save(job);
    }

    private List<String> ansibleCallback(Process process) throws IOException {
        List<String> ansibleResponseList = new ArrayList<>();
        String ansibleResponse = "";
        BufferedReader ansibleReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        LOGGER.debug("ansible deploy command(): -> execution=> :");
        while ((ansibleResponse = ansibleReader.readLine()) != null) {
            LOGGER.debug("{}", ansibleResponse);
            ansibleResponseList.add(ansibleResponse);
        }
        return ansibleResponseList;
    }
}
