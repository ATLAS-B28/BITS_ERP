package com.example.bitserp.auth;

import com.example.bitserp.auth.dto.*;
import com.example.bitserp.shared.dto.ApiResponse;
import com.example.bitserp.shared.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest registerRequest
            ) {
        User user = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully", user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
            ) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(body.get("refresh_token"))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = authService.getJwtService().extractEmail(token);
        String role = authService.getJwtService().extractRole(token);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        Map.of(
                                "email", email,
                                "role", role
                        )
                )
        );
    }
    // Get all pending users
    @GetMapping("/admin/users/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingUsers() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getPendingUsers()));
    }

    // Get all users
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getAllUsers()));
    }

    // Admin updates user role + status
    @PatchMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        authService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok("User updated", null));
    }

    @GetMapping("/hash")
    public String hash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}
