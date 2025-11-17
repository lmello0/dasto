package com.lmello.dasto.budget;

import com.lmello.dasto.budget.dto.input.CreateBudgetDTO;
import com.lmello.dasto.budget.dto.input.PatchBudgetDTO;
import com.lmello.dasto.budget.dto.output.BudgetResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/active")
    public ResponseEntity<BudgetResponse> getActiveBudget(@PathVariable UUID userId) {
        Budget budget = budgetService.getActiveBudget(userId);

        return ResponseEntity.ok(new BudgetResponse(budget));
    }

    @GetMapping
    public ResponseEntity<Page<BudgetResponse>> getBudgetHistory(
            @PathVariable UUID userId,
            Pageable pageable
    ) {
        Page<Budget> budgets = budgetService.getBudgetHistory(userId, pageable);
        Page<BudgetResponse> response = budgets.map(BudgetResponse::new);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @PathVariable UUID userId,
            @RequestBody @Valid CreateBudgetDTO data
    ) {
        Budget budget = budgetService.createBudget(userId, data);
        BudgetResponse response = new BudgetResponse(budget);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/active")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> patchBudget(
            @PathVariable UUID userId,
            @PathVariable Long budgetId,
            @RequestBody @Valid PatchBudgetDTO data
    ) {
        Budget budget = budgetService.putBudget(userId, budgetId, data);
        BudgetResponse response = new BudgetResponse(budget);

        return ResponseEntity.ok(response);
    }
}
