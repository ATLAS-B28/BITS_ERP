package com.example.bitserp.auth;

import com.example.bitserp.auth.dto.*;
import com.example.bitserp.shared.entity.Role;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.RoleRepository;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Getter
    private final JwtService jwtService;
    @Value("${app.jwt.expiry-ms}")
    private long expiryMs;

    public User register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotException("Customer Role Not Found"));

        User user = new User();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);
        user.setActive(false);
        user.setStatus("PENDING");
        return userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotException("User not found: " + request.getEmail()));
        if(!user.getActive()) {
            throw new BadCredentialsException("Account disabled");
        }
        if ("PENDING".equals(user.getStatus())) {
            throw new BadCredentialsException("Account pending admin approval");
        }
        if ("SUSPENDED".equals(user.getStatus())) {
            throw new BadCredentialsException("Account suspended");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong password");
        }
        String role = user.getRole().getName();
        String accessToken = jwtService.genToken(user.getEmail(), role);
        String refreshToken = jwtService.geneRefreshToken(user.getEmail(), role);

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiryMs / 1000,
                role,
                user.getEmail()
        );
    }

    public TokenResponse refresh(String refreshToken) {
        if(!jwtService.isTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotException("User not found: " + email));
        String role = user.getRole().getName();
        String newAccessToken = jwtService.genToken(email, role);
        String newRefreshToken = jwtService.geneRefreshToken(email, role);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                expiryMs / 1000,
                role,
                email
        );
    }

    public void updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("User not found"));

        if (request.getRoleName() != null) {
            Role role = roleRepository.findByName(request.getRoleName())
                    .orElseThrow(() -> new ResourceNotException(
                            "Role not found: " + request.getRoleName()));
            user.setRole(role);
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
            // auto activate/deactivate based on status
            user.setActive("ACTIVE".equals(request.getStatus()));
        }

        userRepository.save(user);
    }

    public List<UserResponse> getPendingUsers() {
        return userRepository.findByStatus("PENDING")
                .stream()
                .map(u -> new UserResponse(
                        u.getId(), u.getName(), u.getEmail(),
                        u.getRole().getName(), u.getStatus(), u.getActive()))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserResponse(
                        u.getId(), u.getName(), u.getEmail(),
                        u.getRole().getName(), u.getStatus(), u.getActive()))
                .collect(Collectors.toList());
    }

}
