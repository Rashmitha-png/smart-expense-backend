package com.smartexpense.controller;

import com.smartexpense.model.Budget;
import com.smartexpense.model.Transaction;
import com.smartexpense.repository.BudgetRepository;
import com.smartexpense.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetRepository budgetRepo;
    private final TransactionRepository txRepo;

    public BudgetController(BudgetRepository budgetRepo, TransactionRepository txRepo) {
        this.budgetRepo = budgetRepo; this.txRepo = txRepo;
    }

    private Map<String, Object> view(Budget b, List<Transaction> monthExpenses) {
        double spent = monthExpenses.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(b.getCategory()))
                .mapToDouble(Transaction::getAmount).sum();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", b.getId());
        m.put("category", b.getCategory());
        m.put("limit", b.getLimit());
        m.put("spent", Math.round(spent * 100.0) / 100.0);
        return m;
    }

    private List<Transaction> monthExpenses() {
        LocalDate now = LocalDate.now();
        return txRepo.findAll().stream()
                .filter(t -> t.getType().equalsIgnoreCase("Expense"))
                .filter(t -> t.getDate().getYear() == now.getYear() && t.getDate().getMonth() == now.getMonth())
                .toList();
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        List<Transaction> me = monthExpenses();
        return budgetRepo.findAll().stream().map(b -> view(b, me)).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> save(@Valid @RequestBody Budget body) {
        Budget b = budgetRepo.findByCategory(body.getCategory()).orElseGet(Budget::new);
        b.setCategory(body.getCategory());
        b.setLimit(body.getLimit());
        return view(budgetRepo.save(b), monthExpenses());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { budgetRepo.deleteById(id); }
}
