package com.ats.resumescreener.model;

import java.util.List;

public class CandidateResult {

    private String name;
    private double score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private int matchedCount;
    private int missingCount;

    public CandidateResult(
            String name,
            double score,
            List<String> matchedSkills,
            List<String> missingSkills) {

        this.name = name;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.matchedCount = matchedSkills.size();
        this.missingCount = missingSkills.size();
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
}
