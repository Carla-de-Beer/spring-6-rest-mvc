package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(CustomerController.BASE_URL)
@RequiredArgsConstructor
@RestController
public class CustomerController {

    public static final String BASE_URL = "/api/v1/customer";

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDto> listAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<String> saveNewCustomer(@RequestBody CustomerDto customer) {
        CustomerDto savedCustomer = customerService.saveNewCustomer(customer);

        val headers = new HttpHeaders();
        headers.add("Location", BASE_URL + "/" + savedCustomer.getId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("{customerId}")
    public ResponseEntity<String> updateCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDto customer) {
        val updatedCustomer = customerService.updateCustomerById(customerId, customer);

        if (updatedCustomer.isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{beerId}")
    public ResponseEntity<String> patchCustomerById(@PathVariable("beerId") UUID customerId, @RequestBody CustomerDto customer) {
        customerService.patchCustomerById(customerId, customer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable("customerId") UUID customerId) {
        if (!customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}