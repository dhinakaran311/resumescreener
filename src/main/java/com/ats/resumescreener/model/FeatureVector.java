package com.ats.resumescreener.model;

import java.util.List;

public class FeatureVector {

    private String candidateName;
    private List<Double> features;

    public FeatureVector(String candidateName, List<Double> features) {
        this.candidateName = candidateName;
        this.features = features;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public List<Double> getFeatures() {
        return features;
    }
}
