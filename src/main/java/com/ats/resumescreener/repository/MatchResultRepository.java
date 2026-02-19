package com.ats.resumescreener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.resumescreener.entity.MatchResult;

public interface MatchResultRepository
        extends JpaRepository<MatchResult, Long> {}
