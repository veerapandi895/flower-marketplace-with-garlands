package com.flowermarket.controller;

import com.flowermarket.entity.Category;
import com.flowermarket.entity.Order;
import com.flowermarket.entity.User;
import com.flowermarket.enums.Role;
import com.flowermarket.repository.OrderRepository;
import com.flowermarket.repository.UserRepository;
import com.flowermarket.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/management")
@RequiredArgsConstructor
public class AdminManagementController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CategoryService categoryService;

    @GetMapping("/sellers")
    public ResponseEntity<List<User>> sellers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SELLER).toList());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<User>> customers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER).toList());
    }

    @PatchMapping("/users/{id}/toggle-enabled")
    public ResponseEntity<User> toggleEnabled(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.flowermarket.exception.ResourceNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> orders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.create(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> categories() {
        return ResponseEntity.ok(categoryService.getAll());
    }
}
