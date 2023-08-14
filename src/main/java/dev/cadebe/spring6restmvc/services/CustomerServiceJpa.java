package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.mappers.CustomerMapper;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJpa implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toModel)
                .toList();
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID uuid) {
        return Optional.ofNullable(customerMapper.toModel(customerRepository.findById(uuid).orElse(null)));
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customer) {
        return customerMapper.toModel(customerRepository.save(customerMapper.toEntity(customer)));
    }

    @Override
    public Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customer) {
        AtomicReference<Optional<CustomerDto>> customerReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(foundCustomer -> {
            foundCustomer.setName(customer.getName());
            customerReference.set(Optional.of(customerMapper.toModel(foundCustomer)));
        }, () -> customerReference.set(Optional.empty()));

        return customerReference.get();
    }

    // TODO
    @Override
    public void patchCustomerById(UUID customerId, CustomerDto customer) {

    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }

        return false;
    }
}
