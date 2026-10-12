package com.smartexpense.config;

import com.smartexpense.model.Budget;
import com.smartexpense.model.Profile;
import com.smartexpense.model.Transaction;
import com.smartexpense.repository.BudgetRepository;
import com.smartexpense.repository.ProfileRepository;
import com.smartexpense.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final TransactionRepository txRepo;
    private final BudgetRepository budgetRepo;
    private final ProfileRepository profileRepo;

    public DataSeeder(TransactionRepository txRepo, BudgetRepository budgetRepo, ProfileRepository profileRepo) {
        this.txRepo = txRepo; this.budgetRepo = budgetRepo; this.profileRepo = profileRepo;
    }

    @Override
    public void run(String... args) {
        if (profileRepo.count() == 0) {
            profileRepo.save(new Profile("Rashmitha", "rashmitha@example.com"));
        }
        if (budgetRepo.count() == 0) {
            budgetRepo.save(new Budget("Food", 6000.0));
            budgetRepo.save(new Budget("Travel", 3000.0));
            budgetRepo.save(new Budget("Shopping", 3000.0));
            budgetRepo.save(new Budget("Bills", 2000.0));
            budgetRepo.save(new Budget("Others", 1500.0));
        }
        if (txRepo.count() == 0) {
            List<Transaction> list = new ArrayList<>();
            // The 5 recent transactions shown on the dashboard (T001..T005)
            list.add(new Transaction(LocalDate.of(2026, 10, 10), "Income", "Salary", "Monthly salary", 25000.0));
            list.add(new Transaction(LocalDate.of(2026, 10, 9), "Expense", "Food", "Lunch at cafe", 450.0));
            list.add(new Transaction(LocalDate.of(2026, 10, 8), "Expense", "Travel", "Bus ticket", 200.0));
            list.add(new Transaction(LocalDate.of(2026, 10, 6), "Expense", "Shopping", "Books purchase", 2200.0));
            list.add(new Transaction(LocalDate.of(2026, 10, 4), "Income", "Freelance", "Project payment", 3500.0));
            // History for the monthly chart (Jan - Sep)
            double[] food = {2500, 2300, 2700, 2400, 2600, 2500, 2200, 2800, 2500};
            for (int m = 1; m <= 9; m++) {
                list.add(new Transaction(LocalDate.of(2026, m, 5), "Income", "Salary", "Monthly salary", 12000.0));
                list.add(new Transaction(LocalDate.of(2026, m, 12), "Expense", "Food", "Groceries and dining", food[m - 1]));
                list.add(new Transaction(LocalDate.of(2026, m, 15), "Expense", "Bills", "Electricity and internet", 1200.0));
                list.add(new Transaction(LocalDate.of(2026, m, 20), "Expense",
                        m % 2 == 0 ? "Travel" : "Shopping", m % 2 == 0 ? "Local travel" : "Clothes", 1500.0));
            }
            txRepo.saveAll(list);
        }
    }
}
