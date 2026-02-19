package com.ats.resumescreener.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double finalScore;
    private double skillScore;
    private double tfidfScore;

    private int matchedCount;
    private int missingCount;
    private int experienceYears;

    @ManyToOne
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Candidate candidate;

    @ManyToOne
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private JobDescription jobDescription;

    public MatchResult() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public double getTfidfScore() {
        return tfidfScore;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public void setSkillScore(double skillScore) {
        this.skillScore = skillScore;
    }

    public void setTfidfScore(double tfidfScore) {
        this.tfidfScore = tfidfScore;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }
}
