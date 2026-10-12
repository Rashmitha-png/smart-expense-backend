package com.smartexpense.controller;

import com.smartexpense.model.Transaction;
import com.smartexpense.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionRepository repo;

    public TransactionController(TransactionRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Transaction> list(@RequestParam(required = false) String type,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String q,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                  @RequestParam(required = false) Integer limit) {
        Stream<Transaction> s = repo.findAll().stream();
        if (type != null && !type.isBlank()) s = s.filter(t -> t.getType().equalsIgnoreCase(type));
        if (category != null && !category.isBlank()) s = s.filter(t -> t.getCategory().equalsIgnoreCase(category));
        if (from != null) s = s.filter(t -> !t.getDate().isBefore(from));
        if (to != null) s = s.filter(t -> !t.getDate().isAfter(to));
        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase();
            s = s.filter(t -> (t.getDescription() != null && t.getDescription().toLowerCase().contains(needle))
                    || t.getCategory().toLowerCase().contains(needle));
        }
        s = s.sorted(Comparator.comparing(Transaction::getDate).reversed()
                .thenComparing(Comparator.comparing(Transaction::getId).reversed()));
        if (limit != null && limit > 0) s = s.limit(limit);
        return s.toList();
    }

    @GetMapping("/{id}")
    public Transaction get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody Transaction t) {
        t.setId(null);
        return repo.save(t);
    }

    @PutMapping("/{id}")
    public Transaction update(@PathVariable Long id, @Valid @RequestBody Transaction body) {
        Transaction t = get(id);
        t.setDate(body.getDate());
        t.setType(body.getType());
        t.setCategory(body.getCategory());
        t.setDescription(body.getDescription());
        t.setAmount(body.getAmount());
        return repo.save(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        repo.deleteById(id);
    }
}
