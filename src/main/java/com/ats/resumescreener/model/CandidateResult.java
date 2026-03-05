package com.ats.resumescreener.model;

import java.util.List;
import java.util.Map;

public class CandidateResult {

    private String name;
    private double score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private int matchedCount;
    private int missingCount;
    private Map<String, Double> categoryScores;
    private ScoreExplanation explanation;

    // Used for early-return (no required skills found)
    public CandidateResult(
            String name,
            double score,
            List<String> matchedSkills,
            List<String> missingSkills,
            Map<String, Double> categoryScores) {

        this.name = name;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.matchedCount = matchedSkills.size();
        this.missingCount = missingSkills.size();
        this.categoryScores = categoryScores;
    }

    // Full constructor with explanation
    public CandidateResult(
            String name,
            double score,
            List<String> matchedSkills,
            List<String> missingSkills,
            Map<String, Double> categoryScores,
            ScoreExplanation explanation) {

        this.name = name;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.matchedCount = matchedSkills.size();
        this.missingCount = missingSkills.size();
        this.categoryScores = categoryScores;
        this.explanation = explanation;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public Map<String, Double> getCategoryScores() {
        return categoryScores;
    }

    public ScoreExplanation getExplanation() {
        return explanation;
    }
}
