package com.example.bitserp.modules.procurement.repository;

import com.example.bitserp.modules.procurement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    List<Vendor> findByActiveTrue();
    List<Vendor> findByLocationCity(String locationCity);
    boolean existsByContactEmail(String contactsEmail);
}
