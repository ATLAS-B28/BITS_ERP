package com.example.bitserp.modules.sales.service;

import com.example.bitserp.modules.sales.dto.CustomerRequest;
import com.example.bitserp.modules.sales.dto.CustomerResponse;
import com.example.bitserp.modules.sales.entity.Customer;
import com.example.bitserp.modules.sales.repository.CustomerRepository;
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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        if(request.getLocationId() != null) {
            Location location = locationRepository.findById(
                    request.getLocationId()
            ).orElseThrow(() -> new ResourceNotException("Location not found"));
            customer.setLocation(location);
        }

        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAllWithLocation()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        return toResponse(customerRepository.findByIdWithLocation(id)
                .orElseThrow(() -> new ResourceNotException("Customer not found")));
    }

    private CustomerResponse toResponse(Customer customer) {
        String city = null;
        String address = null;
        Double lat = null;
        Double lng = null;
        if(customer.getLocation() != null) {
            city = customer.getLocation().getCity();
            address = customer.getLocation().getAddress();
            if(customer.getLocation().getCoordinates() != null) {
                lat = customer.getLocation().getCoordinates().getY();
                lng = customer.getLocation().getCoordinates().getX();
            }
        }
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(), city, address, lat, lng);
    }
}
