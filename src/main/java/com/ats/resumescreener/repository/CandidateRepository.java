package com.ats.resumescreener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.resumescreener.entity.Candidate;

public interface CandidateRepository
        extends JpaRepository<Candidate, Long> {}
