package com.ats.resumescreener.entity;

import jakarta.persistence.*;

@Entity
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    public JobDescription() {}

    public JobDescription(String content) {
        this.content = content;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
}
