package dev.cadebe.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import dev.cadebe.spring6restmvc.services.CustomerService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<UUID> idCaptor;

    @Captor
    ArgumentCaptor<CustomerDto> customerCaptor;

    @Test
    void shouldGetCustomerList() throws Exception {
        val id1 = UUID.randomUUID();
        val id2 = UUID.randomUUID();

        when(customerService.getAllCustomers()).thenReturn(List.of(
                CustomerDto.builder()
                        .id(id1)
                        .name("Customer 1")
                        .version(1)
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .build(),
                CustomerDto.builder()
                        .id(id2)
                        .name("Customer 2")
                        .version(1)
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .build()
        ));

        mockMvc.perform(get(CustomerController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(id1.toString())))
                .andExpect(jsonPath("$.[1].id", is(id2.toString())))
                .andExpect(jsonPath("$..name", is(List.of("Customer 1", "Customer 2"))));
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        val id = UUID.randomUUID();

        when(customerService.getCustomerById(id)).thenReturn(Optional.of(
                CustomerDto.builder()
                        .id(id)
                        .name("Customer X")
                        .version(1)
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .build()));

        mockMvc.perform(get(CustomerController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Customer X")))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    void shouldReturnNotFoundStatusWhenGetCustomerByIdNotFound() throws Exception {
        when(customerService.getCustomerById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        val customer = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Customer X")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(customerService.saveNewCustomer(any(CustomerDto.class))).thenReturn(customer);

        mockMvc.perform(post(CustomerController.BASE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldUpdateExistingCustomerById() throws Exception {
        val id = UUID.randomUUID();
        val customer = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Customer X")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(customerService.updateCustomerById(id, customer)).thenReturn(Optional.of(customer));

        mockMvc.perform(put(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(id, customer);
    }

    @Test
    void shouldFailIfUpdateExistingCustomerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();
        val customer = CustomerDto.builder().build();
        when(customerService.updateCustomerById(id, customer)).thenReturn(Optional.empty());

        mockMvc.perform(put(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());

        verify(customerService).updateCustomerById(id, customer);
    }

    static Stream<Arguments> shouldPatchExistingCustomerById() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(),
                        CustomerDto.builder()
                                .version(1)
                                .name("Customer XZY")
                                .createdDate(LocalDateTime.parse("2023-07-17T17:17:00.00"))
                                .updatedDate(LocalDateTime.parse("2023-07-18T18:18:00.00"))
                                .build()),
                Arguments.of(UUID.randomUUID(),
                        CustomerDto.builder()
                                .version(1)
                                .build()),
                Arguments.of(UUID.randomUUID(),
                        CustomerDto.builder()
                                .build()),
                Arguments.of(UUID.randomUUID(),
                        CustomerDto.builder()
                                .createdDate(LocalDateTime.parse("2023-07-19T19:19:00.00"))
                                .build()),
                Arguments.of(UUID.randomUUID(),
                        CustomerDto.builder()
                                .updatedDate(LocalDateTime.parse("2023-07-20T20:20:00.00"))
                                .build()));
    }

    @ParameterizedTest
    @MethodSource
    void shouldPatchExistingCustomerById(UUID id, CustomerDto customer) throws Exception {
        when(customerService.patchCustomerById(id, customer)).thenReturn(Optional.of(customer));

        mockMvc.perform(patch(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(idCaptor.capture(), customerCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(customerCaptor.getValue()).isEqualTo(customer);
    }

    @Test
    void shouldFailIfPatchExistingCustomerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();
        val customer = CustomerDto.builder().build();
        when(customerService.updateCustomerById(id, customer)).thenReturn(Optional.empty());

        mockMvc.perform(patch(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());

        verify(customerService).patchCustomerById(id, customer);
    }

    @Test
    void shouldDeleteExistingCustomerById() throws Exception {
        val id = UUID.randomUUID();

        when(customerService.deleteCustomerById(id)).thenReturn(true);

        mockMvc.perform(delete(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
    }

    @Test
    void shouldFailWhenDeleteExistingCustomerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();

        when(customerService.deleteCustomerById(id)).thenReturn(false);

        mockMvc.perform(delete(CustomerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}