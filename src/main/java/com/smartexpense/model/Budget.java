package com.smartexpense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category is required")
    @Column(unique = true)
    private String category;

    @NotNull(message = "Limit is required")
    @Positive(message = "Limit must be greater than 0")
    @Column(name = "limit_amount")
    private Double limit;

    public Budget() {}
    public Budget(String category, Double limit) { this.category = category; this.limit = limit; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getLimit() { return limit; }
    public void setLimit(Double limit) { this.limit = limit; }
}
