package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RequestMapping(CustomerController.BASE_URL)
@RequiredArgsConstructor
@RestController
public class CustomerController {

    public static final String BASE_URL = "/api/v1/customers";

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDto> listAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID customerId) {
        val found = customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);

        return ResponseEntity.ok().body(found);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> saveNewCustomer(@RequestBody CustomerDto customer) {
        CustomerDto savedCustomer = customerService.saveNewCustomer(customer);

        val location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{customerId}")
                .buildAndExpand(savedCustomer.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<String> updateCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDto customer) {
        val updatedCustomer = customerService.updateCustomerById(customerId, customer);

        if (updatedCustomer.isEmpty()) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<String> patchCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDto customer) {
        val patchedCustomer = customerService.patchCustomerById(customerId, customer);

        if (patchedCustomer.isEmpty()) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable("customerId") UUID customerId) {
        if (!customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }
}