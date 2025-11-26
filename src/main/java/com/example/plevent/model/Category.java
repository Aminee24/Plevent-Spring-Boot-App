package com.example.plevent.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    // 🎯 FIX 1: Default No-Argument Constructor (Required by JPA/Hibernate)
    public Category() {
    }

    // 🎯 FIX 2: Constructor for Name and Description (Used by DataInitializer)
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}