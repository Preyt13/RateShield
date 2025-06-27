package com.RateShield.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String plan;

    @Column(nullable = false)
    private String rateLimitTier;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    public Organization() {}

    public Organization(String name, String plan) {
        this.name = name;
        this.plan = plan;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getPlan() { return plan; }
    public List<User> getUsers() { return users; }

    public void setName(String name) { this.name = name; }
    public void setPlan(String plan) { this.plan = plan; }
    public void setUsers(List<User> users) { this.users = users; }
}
