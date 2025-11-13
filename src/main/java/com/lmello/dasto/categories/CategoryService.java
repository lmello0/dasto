package com.lmello.dasto.categories;

import com.lmello.dasto.categories.dto.input.CreateCategoryDTO;
import com.lmello.dasto.categories.dto.input.PutCategoryDTO;
import com.lmello.dasto.categories.exceptions.CategoryAlreadyExistsException;
import com.lmello.dasto.categories.exceptions.CategoryDeletionNotPermittedException;
import com.lmello.dasto.categories.exceptions.CategoryNotExistsException;
import com.lmello.dasto.user.User;
import com.lmello.dasto.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public Page<Category> getUserCategories(UUID userId, Pageable pageable) {
        User u = userService.getUserById(userId);
        return categoryRepository.findAllByUser(u, pageable);
    }

    public Category getUserCategory(UUID userId, Long categoryId) {
        User u = userService.getUserById(userId);

        return categoryRepository.findByUserAndId(u, categoryId)
                .orElseThrow(() -> new CategoryNotExistsException(userId, categoryId));
    }

    public Category getUserCategory(User user, Long categoryId) {
        return categoryRepository.findByUserAndId(user, categoryId)
                .orElseThrow(() -> new CategoryNotExistsException(user.getPublicId(), categoryId));
    }

    @Transactional
    public Category createUserCategory(UUID userId, CreateCategoryDTO data) {
        User u = userService.getUserById(userId);

        if (categoryRepository.existsByUserAndName(u, data.name())) {
            throw new CategoryAlreadyExistsException(userId, data.name());
        }

        Category c = new Category();
        c.setName(data.name());
        c.setUser(u);

        u.addCategory(c);

        return categoryRepository.save(c);
    }

    @Transactional
    public Category updateUserCategory(UUID userId, Long categoryId, PutCategoryDTO data) {
        User u = userService.getUserById(userId);

        Category c = categoryRepository.findByUserAndId(u, categoryId)
                .orElseThrow(() -> new CategoryNotExistsException(userId, categoryId));

        if (!c.getName().equals(data.name())
                && categoryRepository.existsByUserAndName(u, data.name())) {
            throw new CategoryAlreadyExistsException(userId, data.name());
        }

        c.setName(data.name());
        return categoryRepository.save(c);
    }

    @Transactional
    public void deleteUserCategory(UUID userId, Long categoryId) {
        User u = userService.getUserById(userId);

        Category c = categoryRepository.findByUserAndId(u, categoryId)
                .orElseThrow(() -> new CategoryNotExistsException(userId, categoryId));

        if (categoryRepository.countExpensesByCategory(c) > 0) {
            throw new CategoryDeletionNotPermittedException(userId, categoryId);
        }

        categoryRepository.delete(c);
    }

    @Transactional
    public void deleteUserCategoryForce(UUID userId, Long categoryId) {
        User u = userService.getUserById(userId);

        Category c = categoryRepository.findByUserAndId(u, categoryId)
                .orElseThrow(() -> new CategoryNotExistsException(userId, categoryId));

        categoryRepository.delete(c);
    }
}
