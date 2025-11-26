package com.example.plevent.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    // --- CONSTRUCTORS (FIX) ---

    // 1. Default constructor (required by JPA/Hibernate)
    public Role() {
    }

    // 2. Constructor to initialize the name (used in DataInitializer)
    public Role(String name) {
        this.name = name;
    }

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}