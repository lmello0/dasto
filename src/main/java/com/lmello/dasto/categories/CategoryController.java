package com.lmello.dasto.categories;

import com.lmello.dasto.categories.dto.input.CreateCategoryDTO;
import com.lmello.dasto.categories.dto.input.PutCategoryDTO;
import com.lmello.dasto.categories.dto.output.CategoryDetailResponse;
import com.lmello.dasto.categories.dto.output.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getUserCategories(
            @PathVariable UUID userId,
            Pageable pageable
    ) {
        Page<Category> rawUserCategories = categoryService.getUserCategories(userId, pageable);
        Page<CategoryResponse> userCategories = rawUserCategories.map(CategoryResponse::new);

        return ResponseEntity.ok(userCategories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponse> getUserCategory(
            @PathVariable UUID userId,
            @PathVariable Long categoryId
    ) {
        Category c = categoryService.getUserCategory(userId, categoryId);
        CategoryDetailResponse categoryDetailResponse = new CategoryDetailResponse(c);

        return ResponseEntity.ok(categoryDetailResponse);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createUserCategory(
            @PathVariable UUID userId,
            @RequestBody @Valid CreateCategoryDTO data
    ) {
        Category c = categoryService.createUserCategory(userId, data);
        CategoryResponse categoryResponse = new CategoryResponse(c);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(c.getId())
                .toUri();

        return ResponseEntity.created(location).body(categoryResponse);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateUserCategory(
            @PathVariable UUID userId,
            @PathVariable Long categoryId,
            @RequestBody @Valid PutCategoryDTO data
    ) {
        Category c = categoryService.updateUserCategory(userId, categoryId, data);
        CategoryResponse categoryResponse = new CategoryResponse(c);

        return ResponseEntity.ok(categoryResponse);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteUserCategory(
            @PathVariable UUID userId,
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean shouldForce
    ) {
        if (shouldForce) {
            categoryService.deleteUserCategoryForce(userId, categoryId);
        } else {
            categoryService.deleteUserCategory(userId, categoryId);
        }

        return ResponseEntity.noContent().build();
    }
}
