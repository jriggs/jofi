package com.jofi.jofi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickers")
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    private String name;

    private String description;

    @Column(name = "aggressive_level")
    private String aggressiveLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified")
    private LocalDateTime modified;

    public Ticker() {}

    // Getters and setters

    public Long getId() { return id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAggressiveLevel() { return aggressiveLevel; }
    public void setAggressiveLevel(String aggressiveLevel) { this.aggressiveLevel = aggressiveLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}
