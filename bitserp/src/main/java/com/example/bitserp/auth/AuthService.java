package com.example.bitserp.auth;

import com.example.bitserp.auth.dto.RegisterRequest;
import com.example.bitserp.shared.entity.Role;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.RoleRepository;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}
