package com.example.bitserp.auth;

import com.example.bitserp.auth.dto.LoginRequest;
import com.example.bitserp.auth.dto.RegisterRequest;
import com.example.bitserp.auth.dto.TokenResponse;
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

        Role role = roleRepository.findByName(registerRequest.getRoleName())
                .orElseThrow(() -> new ResourceNotException("Role not found: " + registerRequest.getRoleName()));

        User user = new User();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotException("User not found: " + request.getEmail()));
        if(!user.getActive()) {
            throw new BadCredentialsException("Account disabled");
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
        if(jwtService.isTokenValid(refreshToken)) {
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

}
