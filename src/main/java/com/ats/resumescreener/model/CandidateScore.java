package com.ats.resumescreener.model;

public class CandidateScore {

    private String name;
    private double score;

    public CandidateScore(String name, double score){
        this.name = name;
        this.score = score;
    }

    public String getName(){
        return name;
    }

    public double getScore(){
        return score;
    }
}
