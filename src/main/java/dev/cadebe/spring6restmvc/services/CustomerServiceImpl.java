package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.CustomerDto;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDto> customerMap;

    public CustomerServiceImpl() {
        val customer1 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .name("Customer 1")
                .version(1)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        val customer2 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .name("Customer 2")
                .version(1)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        val customer3 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .name("Customer 3")
                .version(1)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        customerMap = new HashMap<>();
        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID uuid) {
        return Optional.of(customerMap.get(uuid));
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customer) {
        val savedCustomer = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(customer.getVersion())
                .name(customer.getName())
                .updatedDate(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .build();

        customerMap.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    @Override
    public Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customer) {
        val existing = customerMap.get(customerId);

        existing.setName(customer.getName());
        existing.setVersion(customer.getVersion());
        existing.setCreatedDate(customer.getCreatedDate());
        existing.setUpdatedDate(customer.getUpdatedDate());

        customerMap.put(existing.getId(), existing);

        return Optional.of(existing);
    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerDto customer) {
        val existing = customerMap.get(customerId);

        if (StringUtils.hasText(customer.getName())) {
            existing.setName(customer.getName());
        }

        if (customer.getCreatedDate() != null) {
            existing.setCreatedDate(customer.getCreatedDate());
        }

        if (customer.getUpdatedDate() != null) {
            existing.setUpdatedDate(customer.getUpdatedDate());
        }

        if (customer.getVersion() != null) {
            existing.setVersion(customer.getVersion());
        }
    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        return customerMap.remove(customerId) != null;
    }
}