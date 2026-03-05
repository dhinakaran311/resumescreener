package com.ats.resumescreener.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    public Candidate() {
    }

    public Candidate(String name) {
        this.name = name;
    }

    public Candidate(String name, String resumeText) {
        this.name = name;
        this.resumeText = resumeText;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getResumeText() {
        return resumeText;
    }
}
