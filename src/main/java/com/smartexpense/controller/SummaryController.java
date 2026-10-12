package com.smartexpense.controller;

import com.smartexpense.model.Transaction;
import com.smartexpense.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
    private final TransactionRepository repo;

    public SummaryController(TransactionRepository repo) { this.repo = repo; }

    private static double r2(double v) { return Math.round(v * 100.0) / 100.0; }

    private static double sum(List<Transaction> list, String type) {
        return list.stream().filter(t -> t.getType().equalsIgnoreCase(type)).mapToDouble(Transaction::getAmount).sum();
    }

    @GetMapping
    public Map<String, Object> summary() {
        List<Transaction> all = repo.findAll();
        double income = sum(all, "Income");
        double expenses = sum(all, "Expense");
        LocalDate now = LocalDate.now();
        long thisMonth = all.stream()
                .filter(t -> t.getDate().getYear() == now.getYear() && t.getDate().getMonth() == now.getMonth())
                .count();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalIncome", r2(income));
        m.put("totalExpenses", r2(expenses));
        m.put("balance", r2(income - expenses));
        m.put("thisMonthCount", thisMonth);
        return m;
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> categories() {
        List<Transaction> expenses = repo.findAll().stream()
                .filter(t -> t.getType().equalsIgnoreCase("Expense")).toList();
        double total = expenses.stream().mapToDouble(Transaction::getAmount).sum();
        Map<String, Double> byCat = expenses.stream().collect(
                Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        return byCat.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("category", e.getKey());
                    m.put("amount", r2(e.getValue()));
                    m.put("percent", total == 0 ? 0 : Math.round(e.getValue() / total * 1000.0) / 10.0);
                    return m;
                }).collect(Collectors.toList());
    }

    @GetMapping("/monthly")
    public Map<String, Object> monthly(@RequestParam(required = false) Integer year) {
        List<Transaction> all = repo.findAll();
        int y = year != null ? year
                : all.stream().map(t -> t.getDate().getYear()).max(Integer::compare).orElse(LocalDate.now().getYear());
        List<Transaction> inYear = all.stream().filter(t -> t.getDate().getYear() == y).toList();
        int last = inYear.stream().mapToInt(t -> t.getDate().getMonthValue()).max().orElse(1);
        List<String> labels = new ArrayList<>();
        List<Double> income = new ArrayList<>();
        List<Double> expense = new ArrayList<>();
        for (int mth = 1; mth <= last; mth++) {
            final int mm = mth;
            labels.add(Month.of(mth).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            List<Transaction> inMonth = inYear.stream().filter(t -> t.getDate().getMonthValue() == mm).toList();
            income.add(r2(sum(inMonth, "Income")));
            expense.add(r2(sum(inMonth, "Expense")));
        }
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("year", y);
        res.put("labels", labels);
        res.put("income", income);
        res.put("expense", expense);
        return res;
    }
}
