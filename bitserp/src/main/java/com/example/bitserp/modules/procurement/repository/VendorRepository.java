package com.example.bitserp.modules.procurement.repository;

import com.example.bitserp.modules.procurement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    List<Vendor> findByActiveTrue();
    List<Vendor> findByLocationCity(String locationCity);
    boolean existsByContactEmail(String contactsEmail);
    @Query("SELECT v FROM Vendor v " +
            "LEFT JOIN FETCH v.location " +
            "WHERE v.active = true")
    List<Vendor> findByActiveTrueWithLocation();

    @Query("SELECT v FROM Vendor v " +
            "LEFT JOIN FETCH v.location " +
            "WHERE v.id = :id")
    Optional<Vendor> findByIdWithLocation(@Param("id") UUID id);
}
