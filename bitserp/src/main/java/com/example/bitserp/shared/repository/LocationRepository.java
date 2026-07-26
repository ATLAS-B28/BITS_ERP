package com.example.bitserp.shared.repository;

import com.example.bitserp.shared.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByType(String type);
    List<Location> findByActiveTrue();
    @Query(value = """
        SELECT * FROM locations
          WHERE ST_DWithin(
            coordinates,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326),
            :radiusMeters
          )
          AND active = true
          AND type = :type
        ORDER BY ST_Distance(
            coordinates,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
        )
    """, nativeQuery = true)
    List<Location> findNearbyByType(
           @Param("lon") double latitude,
           @Param("lat") double longitude,
           @Param("radiusMeters") double radiusKm,
           @Param("type") String type);
    @Query(value = """
            SELECT * FROM locations
                    WHERE ST_DWithin(
                        coordinates,
                        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326),
                        :radiusMeters
                    )
                    AND active = true
                    ORDER BY ST_Distance(
                        coordinates,
                        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
                    )
    """, nativeQuery = true)
    List<Location> findNearBy(
            @Param("lon") double latitude,
            @Param("lat") double longitude,
            @Param("radiusMeters") double radiusKm);
}
