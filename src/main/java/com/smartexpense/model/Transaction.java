package com.smartexpense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    @Column(name = "tx_date")
    private LocalDate date;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "Income|Expense", message = "Type must be Income or Expense")
    @Column(name = "tx_type")
    private String type;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    public Transaction() {}

    public Transaction(LocalDate date, String type, String category, String description, Double amount) {
        this.date = date; this.type = type; this.category = category;
        this.description = description; this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
