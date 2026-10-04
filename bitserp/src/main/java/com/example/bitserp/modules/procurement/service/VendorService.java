package com.example.bitserp.modules.procurement.service;

import com.example.bitserp.modules.procurement.dto.VendorRequest;
import com.example.bitserp.modules.procurement.dto.VendorResponse;
import com.example.bitserp.modules.procurement.entity.Vendor;
import com.example.bitserp.modules.procurement.repository.VendorRepository;
import com.example.bitserp.shared.entity.Location;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final LocationRepository locationRepository;

    public VendorResponse createVendor(VendorRequest vendorRequest) {
        Vendor vendor = new Vendor();
        vendor.setName(vendorRequest.getName());
        vendor.setContactEmail(vendorRequest.getContactEmail());
        vendor.setContactPhone(vendor.getContactPhone());

        if(vendorRequest.getLocationId() != null) {
            Location location = locationRepository.findById(vendorRequest.getLocationId())
                    .orElseThrow(() -> new ResourceNotException("Location not found" + vendorRequest.getLocationId()));
            vendor.setLocation(location);
        }

        return toResponse(vendorRepository.save(vendor));
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findByActiveTrueWithLocation()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendorResponse getVendorById(UUID id) {
        return toResponse(vendorRepository.findByIdWithLocation(id)
                .orElseThrow(() -> new ResourceNotException("Vendor not found")));
    }

    public VendorResponse updateVendor(UUID id, VendorRequest vendorRequest) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Vendor not found" + id));

        vendor.setName(vendorRequest.getName());
        vendor.setContactEmail(vendor.getContactEmail());
        vendor.setContactPhone(vendor.getContactPhone());

        if(vendorRequest.getLocationId() != null) {
            Location location = locationRepository.findById(vendorRequest.getLocationId())
                    .orElseThrow(() -> new ResourceNotException("Location not found" + vendorRequest.getLocationId()));
            vendor.setLocation(location);
        }

        return toResponse(vendorRepository.save(vendor));
    }

    public void deactivateVendor(UUID id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Vendor not found" + id));
        vendor.setActive(false);
        vendorRepository.save(vendor);
    }

    private VendorResponse toResponse(Vendor vendor) {
        String city = null;
        String address = null;
        Double lat = null;
        Double lng = null;
        if(vendor.getLocation() != null) {
            city = vendor.getLocation().getCity();
            address = vendor.getLocation().getAddress();
            if(vendor.getLocation().getCoordinates() != null) {
                lat = vendor.getLocation().getCoordinates().getY();
                lng = vendor.getLocation().getCoordinates().getX();
            }
        }
        String type = vendor.getLocation() != null ? vendor.getLocation().getType() : null;
        return new VendorResponse(
                vendor.getId(), vendor.getName(), vendor.getContactEmail(),
                vendor.getContactPhone(), city, type, address, lat, lng, vendor.getActive()
        );
    }
}
