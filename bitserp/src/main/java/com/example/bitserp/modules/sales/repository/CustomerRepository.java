package com.example.bitserp.modules.sales.repository;

import com.example.bitserp.modules.sales.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLocationCity(String locationCity);
    boolean existsByEmail(String email);
}
