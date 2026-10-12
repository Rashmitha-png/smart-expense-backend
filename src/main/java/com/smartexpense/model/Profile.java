package com.smartexpense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    private Long id = 1L;

    @NotBlank(message = "Name is required")
    private String name;

    private String email;

    public Profile() {}
    public Profile(String name, String email) { this.name = name; this.email = email; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
