package com.maat.bestbuy.integration.repository;

import com.maat.bestbuy.integration.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("jobRepository")
public interface JobRepository extends JpaRepository<Job, String> {
}
