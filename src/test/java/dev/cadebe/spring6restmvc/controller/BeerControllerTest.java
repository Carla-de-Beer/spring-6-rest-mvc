package dev.cadebe.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.services.BeerService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.cadebe.spring6restmvc.model.BeerStyle.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @Captor
    ArgumentCaptor<UUID> idCaptor;

    @Captor
    ArgumentCaptor<BeerDto> beerCaptor;

    @Test
    void shouldGetBeerList() throws Exception {
        val id1 = UUID.randomUUID();
        val id2 = UUID.randomUUID();

        when(beerService.listBeers(any(), any(), any())).thenReturn(
                List.of(
                        BeerDto.builder()
                                .id(id1)
                                .version(1)
                                .beerName("Galaxy Cat")
                                .beerStyle(PALE_ALE)
                                .upc("12345")
                                .price(new BigDecimal("12.99"))
                                .quantityOnHand(122)
                                .createdDate(LocalDateTime.now())
                                .updatedDate(LocalDateTime.now())
                                .build(),
                        BeerDto.builder()
                                .id(id2)
                                .version(1)
                                .beerName("Crank")
                                .beerStyle(IPA)
                                .upc("67890")
                                .price(new BigDecimal("7.99"))
                                .quantityOnHand(392)
                                .createdDate(LocalDateTime.now())
                                .updatedDate(LocalDateTime.now())
                                .build()));

        mockMvc.perform(get(BeerController.BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(id1.toString())))
                .andExpect(jsonPath("$.[1].id", is(id2.toString())))
                .andExpect(jsonPath("$..beerName", is(List.of("Galaxy Cat", "Crank"))));
    }

    @Test
    void shouldGetBeerById() throws Exception {
        val id = UUID.randomUUID();

        when(beerService.getBeerbyId(id)).thenReturn(
                Optional.of(
                        BeerDto.builder()
                                .id(id)
                                .version(1)
                                .beerName("Czech Brew")
                                .beerStyle(PILSNER)
                                .upc("24680")
                                .price(new BigDecimal("5.99"))
                                .quantityOnHand(158)
                                .createdDate(LocalDateTime.now())
                                .updatedDate(LocalDateTime.now())
                                .build()));

        mockMvc.perform(get(BeerController.BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.beerName", is("Czech Brew")))
                .andExpect(jsonPath("$.beerStyle", is(PILSNER.name())))
                .andExpect(jsonPath("$.upc", is("24680")))
                .andExpect(jsonPath("$.price", is(new BigDecimal("5.99").doubleValue())))
                .andExpect(jsonPath("$.quantityOnHand", is(158)));
    }

    @Test
    void shouldReturnNotFoundStatusWhenGetBeerByIdNotFound() throws Exception {
        when(beerService.getBeerbyId(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(BeerController.BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewBeer() throws Exception {
        val id = UUID.randomUUID();
        val createDate = LocalDateTime.now();
        val updatedDate = LocalDateTime.now();

        val beer = BeerDto.builder()
                .version(1)
                .id(id)
                .beerName("Galaxy Cat")
                .beerStyle(PALE_ALE)
                .upc("67890")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(122)
                .createdDate(createDate)
                .updatedDate(updatedDate)
                .build();

        when(beerService.saveNewBeer(any(BeerDto.class))).thenReturn(beer);

        mockMvc.perform(post(BeerController.BASE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldFailNewBeerCreationIfBeerNameIsEmpty() throws Exception {
        val beer = BeerDto.builder().build();

        when(beerService.saveNewBeer(any(BeerDto.class))).thenReturn(beer);

        mockMvc.perform(post(BeerController.BASE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(jsonPath("$.length()", is(4)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateExistingBeerById() throws Exception {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder()
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(IPA)
                .upc("35791")
                .price(new BigDecimal("7.99"))
                .quantityOnHand(27)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(beerService.updateBeerById(id, beer)).thenReturn(Optional.of(beer));

        mockMvc.perform(put(BeerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(id, beer);
    }

    @Test
    void shouldFailIfUpdateExistingBeerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder().build();
        when(beerService.updateBeerById(id, beer)).thenReturn(Optional.empty());

        mockMvc.perform(put(BeerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());

        verify(beerService).updateBeerById(id, beer);
    }

    static Stream<Arguments> shouldPathExistingBeerById() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .version(1)
                        .beerName("Galaxy Cat")
                        .beerStyle(ALE)
                        .upc("75310")
                        .price(new BigDecimal("18.99"))
                        .quantityOnHand(144)
                        .createdDate(LocalDateTime.parse("2023-07-18T08:21:00.00"))
                        .updatedDate(LocalDateTime.parse("2023-07-21T19:34:50.00"))
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .version(1)
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .beerName("Mango Bobs")
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .beerStyle(PORTER)
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .upc("42")
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .price(new BigDecimal("9.99"))
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .quantityOnHand(142)
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .createdDate(LocalDateTime.parse("2023-07-29T12:34:00.00"))
                        .build()),
                Arguments.of(UUID.randomUUID(), BeerDto.builder()
                        .updatedDate(LocalDateTime.parse("2023-07-31T17:46:12.00"))
                        .build()));
    }

    @ParameterizedTest
    @MethodSource
    void shouldPathExistingBeerById(UUID id, BeerDto beer) throws Exception {
        mockMvc.perform(patch(BeerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(idCaptor.capture(), beerCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(beerCaptor.getValue()).isEqualTo(beer);
    }

    @Test
    void shouldDeleteExistingBeerById() throws Exception {
        val id = UUID.randomUUID();

        when(beerService.deleteBeerById(id)).thenReturn(true);

        mockMvc.perform(delete(BeerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteBeerById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
    }

    @Test
    void shouldFailIfDeleteByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();

        when(beerService.deleteBeerById(id)).thenReturn(false);

        mockMvc.perform(delete(BeerController.BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}