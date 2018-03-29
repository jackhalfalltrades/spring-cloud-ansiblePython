package com.maat.bestbuy.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "JOB_MASTER")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "ID")
    private String id;

    @Column(name = "TYPE")
    private String jobType;

    @Lob
    @Column(name = "JOB_PARAM")
    private byte[] param;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIME_STAMP")
    private Date createdTimeStamp;

    @Column(name = "REQUEST_TYPE")
    private String requestType;

    @Column(name = "REQUEST_NUMBER")
    private String requestNumber;

    @Column(name = "APPLICATION")
    private String application;

    @Column(name = "SUBMITTED_BY")
    private String submittedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBMITTED_TIME_STAMP")
    private Date submittedTimeStamp;

    @Column(name="JOB_STATUS")
    private String jobStatus;

    @Column(name = "IS_SCHEDUED")
    private String isScheduled;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SCHEDULED_START_TIME_STAMP")
    private Date scheduledStartTimeStamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SCHEDULED_END_TIME_STAMP")
    private Date scheduledEndTimeStamp;

}
