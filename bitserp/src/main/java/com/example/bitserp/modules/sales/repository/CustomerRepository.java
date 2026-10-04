package com.example.bitserp.modules.sales.repository;

import com.example.bitserp.modules.sales.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLocationCity(String locationCity);
    boolean existsByEmail(String email);
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.location")
    List<Customer> findAllWithLocation();

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.location WHERE c.id = :id")
    Optional<Customer> findByIdWithLocation(@Param("id") UUID id);
}
