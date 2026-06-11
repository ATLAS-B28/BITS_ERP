package com.example.bitserp.auth;

import com.example.bitserp.auth.dto.RegisterRequest;
import com.example.bitserp.shared.dto.ApiResponse;
import com.example.bitserp.shared.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(
            @AuthenticationPrincipal Jwt jwt
            ) {
        assert jwt.getExpiresAt() != null;
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "email", jwt.getSubject(),
                "roles", jwt.getClaim("roles"),
                "expiresAt", jwt.getExpiresAt()
        )));
    }
    @GetMapping("/hash")
    public String hash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}
