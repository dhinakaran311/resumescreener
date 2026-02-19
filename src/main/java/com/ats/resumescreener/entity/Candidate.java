package com.ats.resumescreener.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Candidate() {}

    public Candidate(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
