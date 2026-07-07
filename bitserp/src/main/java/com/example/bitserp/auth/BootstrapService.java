//package com.example.bitserp.auth;
//
//import com.example.bitserp.shared.entity.Role;
//import com.example.bitserp.shared.entity.User;
//import com.example.bitserp.shared.repository.RoleRepository;
//import com.example.bitserp.shared.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@RequiredArgsConstructor
//public class BootstrapService {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${app.bootstrap.enabled}")
//    private boolean bootstrapEnabled;
//
//    @Value("${app.bootstrap.admin-email}")
//    private String adminEmail;
//
//    @Value("${app.bootstrap.admin-password}")
//    private String adminPassword;
//
//    @Bean
//    public ApplicationRunner bootstrapAdmin() {
//        return args -> {
//          if(!bootstrapEnabled) return;
//          if(userRepository.existsByEmail(adminEmail)) {
//              System.out.println(adminEmail);
//              return;
//          }
//            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role not found"));
//            User admin = new User();
//            admin.setName("System Admin - Aditya Bhambere");
//            admin.setEmail(adminEmail);
//            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
//            admin.setRole(adminRole);
//            admin.setActive(true);
//            userRepository.save(admin);
//            System.out.println(">>>>> Admin user created: " + admin);
//        };
//    }
//}
