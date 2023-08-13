package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.CustomerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDto> getAllCustomers();

    Optional<CustomerDto> getCustomerById(UUID uuid);

    CustomerDto saveNewCustomer(CustomerDto customer);

    Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customer);

    void patchCustomerById(UUID customerId, CustomerDto customer);

    boolean deleteCustomerById(UUID customerId);
}
