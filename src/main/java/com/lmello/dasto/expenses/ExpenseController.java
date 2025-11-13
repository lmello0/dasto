package com.lmello.dasto.expenses;

import com.lmello.dasto.expenses.dto.input.CreateExpenseDTO;
import com.lmello.dasto.expenses.dto.input.UpdateExpenseDTO;
import com.lmello.dasto.expenses.dto.output.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getUserExpenses(
            @PathVariable UUID userId,
            Pageable pageable
    ) {
        Page<Expense> expenses = expenseService.getUserExpenses(userId, pageable);
        Page<ExpenseResponse> response = expenses.map(ExpenseResponse::new);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDate(
            @PathVariable UUID userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Expense> expenses = expenseService.getExpensesByDate(userId, date);
        List<ExpenseResponse> response = expenses.stream()
                .map(ExpenseResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Expense> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        List<ExpenseResponse> response = expenses.stream()
                .map(ExpenseResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategory(
            @PathVariable UUID userId,
            @PathVariable Long categoryId,
            Pageable pageable
    ) {
        Page<Expense> expenses = expenseService.getExpensesByCategory(userId, categoryId, pageable);
        Page<ExpenseResponse> response = expenses.map(ExpenseResponse::new);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpense(
            @PathVariable UUID userId,
            @PathVariable Long expenseId
    ) {
        Expense expense = expenseService.getExpenseById(userId, expenseId);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @PathVariable UUID userId,
            @RequestBody @Valid CreateExpenseDTO data
    ) {
        Expense expense = expenseService.createExpense(userId, data);
        ExpenseResponse response = new ExpenseResponse(expense);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(expense.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable UUID userId,
            @PathVariable Long expenseId,
            @RequestBody @Valid UpdateExpenseDTO data
    ) {
        Expense expense = expenseService.updateExpense(userId, expenseId, data);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable UUID userId,
            @PathVariable Long expenseId
    ) {
        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.noContent().build();
    }
}
