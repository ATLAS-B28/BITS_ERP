package com.example.bitserp.gis.service;

import com.example.bitserp.gis.dto.EnrichedLocationResponse;
import com.example.bitserp.gis.dto.LocationRequest;
import com.example.bitserp.gis.dto.LocationResponse;
import com.example.bitserp.gis.dto.NearbyRequest;
import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.inventory.repository.InventoryRepository;
import com.example.bitserp.modules.procurement.entity.Vendor;
import com.example.bitserp.modules.procurement.repository.VendorRepository;
import com.example.bitserp.modules.sales.entity.Customer;
import com.example.bitserp.modules.sales.repository.CustomerRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GisService {

    private final LocationRepository locationRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
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

    public List<EnrichedLocationResponse> getEnrichedLocations() {
        List<Location> locations = locationRepository.findByActiveTrue();

        Map<Integer, String> vendorByLocation = vendorRepository.findByActiveTrue()
                .stream()
                .filter(v -> v.getLocation() != null)
                .collect(Collectors.toMap(
                        v -> v.getLocation().getId(),
                        Vendor::getName,
                        (a,b) -> a
                ));

        Map<Integer, String> customerByLocation = customerRepository.findAll()
                .stream()
                .filter(c -> c.getLocation() != null)
                .collect(Collectors.toMap(
                        c -> c.getLocation().getId(),
                        Customer::getName,
                        (a, b) -> a
                ));

        return locations.stream().map(loc -> {
            Double lat = loc.getCoordinates() != null
                    ? loc.getCoordinates().getY()  : null;
            Double lon = loc.getCoordinates() != null
                    ? loc.getCoordinates().getX() : null;

            String ownerName = null;
            String ownerType = "INTERNAL";

            if(vendorByLocation.containsKey(loc.getId())) {
                ownerName = vendorByLocation.get(loc.getId());
                ownerType = "VENDOR";
            } else if(customerByLocation.containsKey(loc.getId())) {
                ownerName = customerByLocation.get(loc.getId());
                ownerType = "CUSTOMER";
            }

            boolean hasLowStock = false;
            int totalStock = 0;

            if("warehouse".equals(loc.getId())) {
                List<com.example.bitserp.modules.inventory.entity.Inventory> inv =
                        inventoryRepository.findByLocationId(loc.getId());
                totalStock = inv.stream()
                        .mapToInt(Inventory::getQuantity).sum();
                hasLowStock = inv.stream()
                        .anyMatch(i -> i.getQuantity() <= i.getReorderLevel());
            }

            return new EnrichedLocationResponse(
                    loc.getId(), loc.getName(), loc.getType(),
                    ownerName, ownerType,
                    loc.getAddress(), loc.getCity(), loc.getState(),
                    lat, lon, loc.getActive(), hasLowStock, totalStock
            );
        }).collect(Collectors.toList());
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
