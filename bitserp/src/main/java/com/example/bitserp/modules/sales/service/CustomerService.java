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
        return customerRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID customerId) {
        return toResponse(customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotException("Customer not found"))
        );
    }

    private CustomerResponse toResponse(Customer customer) {
        String city = customer.getLocation() != null ? customer.getLocation().getCity() : null;
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(), city);
    }
}
