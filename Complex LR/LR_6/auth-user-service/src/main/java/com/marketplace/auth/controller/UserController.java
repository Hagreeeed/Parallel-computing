package com.marketplace.auth.controller;

import com.marketplace.auth.dto.request.BecomeSellerRequest;
import com.marketplace.auth.dto.request.CreateUserRequest;
import com.marketplace.auth.model.Seller;
import com.marketplace.auth.model.User;
import com.marketplace.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAll(Pageable pageable) { return ResponseEntity.ok(userService.findAll(pageable)); }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getById(@PathVariable Long id) { return ResponseEntity.ok(userService.findById(id)); }

    @PostMapping("/{id}/become-seller")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Seller> becomeSeller(@PathVariable Long id, @Valid @RequestBody BecomeSellerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.becomeSeller(id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { userService.delete(id); return ResponseEntity.noContent().build(); }
}
