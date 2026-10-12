package com.smartexpense.controller;

import com.smartexpense.model.Profile;
import com.smartexpense.repository.ProfileRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileRepository repo;

    public ProfileController(ProfileRepository repo) { this.repo = repo; }

    @GetMapping
    public Profile get() {
        return repo.findById(1L).orElseGet(() -> repo.save(new Profile("Rashmitha", "")));
    }

    @PutMapping
    public Profile update(@Valid @RequestBody Profile body) {
        Profile p = get();
        p.setName(body.getName());
        p.setEmail(body.getEmail());
        return repo.save(p);
    }
}
