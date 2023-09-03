package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.mappers.CustomerMapper;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerServiceJpaIT {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerServiceJpa customerService;

    @Test
    void shouldListAllCustomers() {
        val result = customerService.getAllCustomers();

        assertThat(result).hasSize(3);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListIfNoCustomersPresent() {
        customerRepository.deleteAll();

        val result = customerService.getAllCustomers();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetCustomerById() {
        val first = customerRepository.findAll().get(0);

        val result = customerService.getCustomerById(first.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        val result = customerService.getCustomerById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void shouldSaveNewCustomer() {
        val customersBefore = customerRepository.findAll();

        val customer = CustomerDto.builder()
                .name("Customer XYZ")
                .email("customerxzy@xzy.com")
                .build();

        val start = LocalDateTime.now();
        val savedCustomer = customerService.saveNewCustomer(customer);
        val finish = LocalDateTime.now();

        val customersAfter = customerRepository.findAll();

        assertThat(customersAfter.size() - customersBefore.size()).isEqualTo(1);
        assertThat(savedCustomer)
                .extracting(CustomerDto::getName, CustomerDto::getEmail)
                .containsExactly("Customer XYZ", "customerxzy@xzy.com");

        // TODO
        // customerRepository.flush();
        // assertThat(savedCustomer.getCreatedDate()).isBetween(start, finish);
        // assertThat(savedCustomer.getUpdatedDate()).isBetween(start, finish);
        // assertThat(savedCustomer.getVersion()).isEqualTo(1);
    }

    static Stream<CustomerDto> shouldUpdateExistingCustomerById() {
        return Stream.of(
                CustomerDto.builder()
                        .name("Customer ABC")
                        .build(),
                CustomerDto.builder()
                        .name("Customer DEF")
                        .email("customerdef@def.com")
                        .build());
    }

    @ParameterizedTest
    @MethodSource
    @Transactional
    @Rollback
    void shouldUpdateExistingCustomerById(CustomerDto customerDto) {
        val first = customerRepository.findAll().get(0);

        assertThat(first)
                .extracting(CustomerEntity::getName, CustomerEntity::getEmail)
                .containsExactly("Customer 1", "123@abc.com");

        val existing = customerMapper.toModel(first);
        existing.setName(customerDto.getName());
        existing.setEmail(customerDto.getEmail());

        val result = customerService.updateCustomerById(first.getId(), existing);

        assertThat(result).isNotEmpty();
    }

    @Test
    @Transactional
    void shouldNotUpdateCustomerWhenNotFound() {
        val customer = CustomerDto.builder()
                .name("Customer ABC")
                .email("customerabc@abc.com")
                .build();

        val result = customerService.updateCustomerById(UUID.randomUUID(), customer);

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void shouldDeleteCustomerById() {
        val first = customerRepository.findAll().get(0);

        customerService.deleteCustomerById(first.getId());

        assertThat(customerRepository.findById(first.getId())).isEmpty();
    }

    @Test
    @Transactional
    void shouldNotDeleteCustomerWhereCustomerIsNotFound() {
        val id = UUID.randomUUID();

        customerService.deleteCustomerById(id);

        assertThat(customerRepository.findById(id)).isEmpty();
    }
}