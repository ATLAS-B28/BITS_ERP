package com.example.bitserp.shared.repository;

import com.example.bitserp.shared.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByType(String type);
    List<Location> findByActiveTrue();
}
