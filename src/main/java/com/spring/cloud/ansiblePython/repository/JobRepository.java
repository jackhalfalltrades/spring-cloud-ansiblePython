package com.spring.cloud.ansiblePython.repository;

import com.spring.cloud.ansiblePython.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("jobRepository")
public interface JobRepository extends JpaRepository<Job, String> {
}
