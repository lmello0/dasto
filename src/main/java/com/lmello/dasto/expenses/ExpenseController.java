package com.lmello.dasto.expenses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/expenses")
public class ExpenseController {

    @GetMapping
    public ResponseEntity<?> getExpenses(@PathVariable String userId) {
        return ResponseEntity.ok("ping");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(@PathVariable String userId) {
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@PathVariable String userId) {
        throw new UnsupportedOperationException();
    }

    @PatchMapping
    public ResponseEntity<?> updateExpense(@PathVariable String userId) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExpense(@PathVariable String userId) {
        throw new UnsupportedOperationException();
    }
}
