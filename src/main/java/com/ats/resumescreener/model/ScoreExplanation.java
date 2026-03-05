package com.ats.resumescreener.model;

import java.util.Map;

public class ScoreExplanation {

    private double skillScore;
    private double tfidfScore;
    private int experienceYears;
    private double experienceBoost;
    private Map<String, Double> categoryScores;

    public ScoreExplanation(
            double skillScore,
            double tfidfScore,
            int experienceYears,
            double experienceBoost,
            Map<String, Double> categoryScores) {

        this.skillScore = skillScore;
        this.tfidfScore = tfidfScore;
        this.experienceYears = experienceYears;
        this.experienceBoost = experienceBoost;
        this.categoryScores = categoryScores;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public double getTfidfScore() {
        return tfidfScore;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public double getExperienceBoost() {
        return experienceBoost;
    }

    public Map<String, Double> getCategoryScores() {
        return categoryScores;
    }
}
