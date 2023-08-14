package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.mappers.CustomerMapper;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void shouldGetCustomerList() {
        val customers = customerController.listAllCustomers();

        assertThat(customers).hasSize(3);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListIfNoCustomersFound() {
        customerRepository.deleteAll();

        val customers = customerController.listAllCustomers();

        assertThat(customers).isEmpty();
    }

    @Test
    void shouldGetCustomerById() {
        val customer = customerRepository.findAll().get(0);

        val found = customerController.getCustomerById(customer.getId());

        assertThat(found).isNotNull();
    }

    @Test
    void shouldFailWhenGetCustomerByIdReturnsEmpty() {
        val id = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(id));
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveNewCustomer() {
        val name = "Customer XYZ";
        assertThat(customerRepository.findAll()).hasSize(3);

        val customer = CustomerDto.builder()
                .name(name)
                .build();

        val result = customerController.saveNewCustomer(customer);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getHeaders().getLocation()).isNotNull();

        val pathSegments = result.getHeaders().getLocation().getPath().split("/");
        val savedCustomer = customerRepository.findById(UUID.fromString(pathSegments[4]));

        assertThat(savedCustomer.get()).isNotNull();
        assertThat(savedCustomer.get().getName()).isEqualTo(name);
        assertThat(customerRepository.findAll()).hasSize(4);
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateCustomerById() {
        val name = "Customer XYZ";
        val customer = customerRepository.findAll().get(0);
        val customerDto = customerMapper.toModel(customer);
        customerDto.setName(name);

        val result = customerController.updateCustomerById(customer.getId(), customerDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        val updated = customerRepository.findById(customer.getId());
        assertThat(updated.get().getName()).isEqualTo(name);
    }

    @Test
    void shouldFailUpdateCustomerByIdWhenCustomerNotFound() {
        val id = UUID.randomUUID();
        val customer = CustomerDto.builder().build();

        assertThrows(NotFoundException.class, () -> customerController.updateCustomerById(id, customer));
    }

    // TODO: Add patch test

    @Test
    @Transactional
    @Rollback
    void shouldDeleteCustomerById() {
        val customerId = customerRepository.findAll().get(0).getId();
        val found = customerRepository.findById(customerId);
        assertThat(found).isNotEmpty();

        val result = customerController.deleteCustomerById(customerId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        val existing = customerRepository.findById(customerId);
        assertThat(existing).isEmpty();
    }

    @Test
    void shouldFailDeleteCustomerByIdWhenCustomerNotFound() {
        val id = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> customerController.deleteCustomerById(id));
    }
}