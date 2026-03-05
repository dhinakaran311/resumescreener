package com.ats.resumescreener.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ats.resumescreener.entity.MatchResult;

public interface MatchResultRepository
                extends JpaRepository<MatchResult, Long> {

        List<MatchResult> findByFinalScoreGreaterThanEqual(double score);

        List<MatchResult> findByExperienceYearsGreaterThanEqual(int years);

        List<MatchResult> findTop5ByOrderByFinalScoreDesc();
}
