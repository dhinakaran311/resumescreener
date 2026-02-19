
package com.ats.resumescreener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.resumescreener.entity.JobDescription;

public interface JobDescriptionRepository
        extends JpaRepository<JobDescription, Long> {}
