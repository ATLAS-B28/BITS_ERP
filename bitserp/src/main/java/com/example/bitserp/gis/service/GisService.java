package com.example.bitserp.gis.service;

import com.example.bitserp.gis.dto.LocationRequest;
import com.example.bitserp.gis.dto.LocationResponse;
import com.example.bitserp.gis.dto.NearbyRequest;
import com.example.bitserp.shared.entity.Location;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GisService {

    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public LocationResponse createLocation(LocationRequest locationRequest) {
        Location location = new Location();
        location.setName(locationRequest.getName());
        location.setType(locationRequest.getType());
        location.setAddress(locationRequest.getAddress());
        location.setCity(locationRequest.getCity());
        location.setState(locationRequest.getState());
        location.setCountry(locationRequest.getCountry() != null ? locationRequest.getCountry() : "India");
        location.setActive(true);

        if(locationRequest.getLatitude() != null && locationRequest.getLongitude() != null) {
            Point point = geometryFactory.createPoint(
                    new Coordinate(locationRequest.getLongitude(), locationRequest.getLatitude())
            );
            location.setCoordinates(point);
        }
        return toResponse(locationRepository.save(location));
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getByType(String type) {
        return locationRepository.findByType(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LocationResponse> findNearBy(NearbyRequest nearbyRequest) {
        double radiusMeters = nearbyRequest.getRadiusKm() * 1000;
        List<Location> results;

        if(nearbyRequest.getType() != null) {
            results = locationRepository.findNearbyByType(
                    nearbyRequest.getLongitude(),
                    nearbyRequest.getLatitude(),
                    radiusMeters,
                    nearbyRequest.getType()
            );
        } else {
            results = locationRepository.findNearBy(
                    nearbyRequest.getLongitude(),
                    nearbyRequest.getLatitude(),
                    radiusMeters
            );
        }

        return results
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LocationResponse findById(Integer id) {
        return toResponse(locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Location with id " + id + " not found")));
    }

    private LocationResponse toResponse(Location location) {
        Double lat = null;
        Double lon = null;

        if(location.getCoordinates() != null) {
            lat = location.getCoordinates().getY();
            lon = location.getCoordinates().getX();
        }

        return new LocationResponse(
                location.getId(), location.getName(), location.getType(),
                location.getAddress(), location.getCity(), location.getState(),
                location.getCountry(), lat, lon, location.getActive()
        );
    }
}
