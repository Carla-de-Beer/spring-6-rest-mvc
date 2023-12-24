package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.annotations.AdminUser;
import dev.cadebe.spring6restmvc.annotations.StandardUser;
import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.mappers.CustomerMapper;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @AdminUser
    void shouldSaveNewCustomerForAnAdminUser() {
        val name = "Customer XYZ";
        assertThat(customerRepository.findAll()).hasSize(3);

        val customer = CustomerDto.builder()
                .name(name)
                .build();

        val result = customerController.saveNewCustomer(customer);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getHeaders().getLocation()).isNotNull();

        val id = result.getHeaders().getLocation().getPath().substring(1);
        val savedCustomer = customerRepository.findById(UUID.fromString(id));

        assertThat(savedCustomer.get()).isNotNull();
        assertThat(savedCustomer.get().getName()).isEqualTo(name);
        assertThat(customerRepository.findAll()).hasSize(4);
    }

    @Test
    @Transactional
    @StandardUser
    void shouldReturnAccessDeniedWhenAttemptingToSaveNewCustomerForUnAuthorizedUser() {
        val name = "Customer XYZ";
        val customer = CustomerDto.builder()
                .name(name)
                .build();

        assertThrows(AccessDeniedException.class, () -> customerController.saveNewCustomer(customer));
    }

    @Test
    @Transactional
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

    @Test
    @Transactional
    void shouldPatchCustomerById() {
        val customer = customerRepository.findAll().get(0);
        val id = customer.getId();
        val customerDto = customerMapper.toModel(customer);

        assertThat(customer)
                .extracting(CustomerEntity::getName, CustomerEntity::getEmail)
                .containsExactly("Customer 1", "123@abc.com");

        customerDto.setName("Some new custome name");
        customerDto.setEmail("357@jkl.com");

        val result = customerController.patchCustomerById(id, customerDto);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        val patched = customerRepository.findById(id);

        assertThat(patched.get())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(customerMapper.toEntity(customerDto));
    }

    @Test
    void shouldFailPatchByIdIfCustomerNotFound() {
        val id = UUID.randomUUID();
        val customer = CustomerDto.builder().build();

        assertThrows(NotFoundException.class, () -> customerController.patchCustomerById(id, customer));
    }

    @Test
    @Transactional
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