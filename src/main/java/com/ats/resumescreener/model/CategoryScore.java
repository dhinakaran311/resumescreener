package com.ats.resumescreener.model;

public class CategoryScore {

    private String category;
    private double score;

    public CategoryScore(String category, double score) {
        this.category = category;
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public double getScore() {
        return score;
    }
}
