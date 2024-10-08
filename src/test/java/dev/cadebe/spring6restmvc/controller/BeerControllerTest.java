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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.cadebe.spring6restmvc.controller.BeerController.BASE_URL;
import static dev.cadebe.spring6restmvc.model.BeerStyle.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@AutoConfigureMockMvc(addFilters = false)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerService beerService;

    @Captor
    private ArgumentCaptor<UUID> idCaptor;

    @Captor
    private ArgumentCaptor<BeerDto> beerCaptor;

    @Test
    void shouldGetBeerList() throws Exception {
        val id1 = UUID.randomUUID();
        val id2 = UUID.randomUUID();

        when(beerService.listBeers(any(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(
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
                                        .build())));

        val result = mockMvc.perform(get(BASE_URL)
                .accept(APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
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

        val result = mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk());

        result.andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.beerName", is("Czech Brew")))
                .andExpect(jsonPath("$.beerStyle", is(PILSNER.name())))
                .andExpect(jsonPath("$.upc", is("24680")))
                .andExpect(jsonPath("$.price", is(new BigDecimal("5.99").doubleValue())))
                .andExpect(jsonPath("$.quantityOnHand", is(158)));
    }

    @Test
    void shouldReturnSuccessfullyWhenCorrectBeerStyleEnumProvided() throws Exception {
        val id1 = UUID.randomUUID();
        val id2 = UUID.randomUUID();

        when(beerService.listBeers(any(), eq(SAISON), any(), any(), any())).thenReturn(
                new PageImpl<>(
                        List.of(
                                BeerDto.builder()
                                        .id(id1)
                                        .version(1)
                                        .beerName("Alaskan Sky")
                                        .beerStyle(SAISON)
                                        .upc("67894")
                                        .price(new BigDecimal("8.90"))
                                        .quantityOnHand(122)
                                        .createdDate(LocalDateTime.now())
                                        .updatedDate(LocalDateTime.now())
                                        .build(),
                                BeerDto.builder()
                                        .id(id2)
                                        .version(1)
                                        .beerName("Northern Delight")
                                        .beerStyle(SAISON)
                                        .upc("67428")
                                        .price(new BigDecimal("6.95"))
                                        .quantityOnHand(392)
                                        .createdDate(LocalDateTime.now())
                                        .updatedDate(LocalDateTime.now())
                                        .build())));

        val result = mockMvc.perform(get(BASE_URL)
                        .queryParam("beerStyle", "Saison")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(id1.toString())))
                .andExpect(jsonPath("$.[1].id", is(id2.toString())))
                .andExpect(jsonPath("$..beerName", is(List.of("Alaskan Sky", "Northern Delight"))));
    }

    @Test
    void shouldThrowReturnBadRequestWhenIncorrectBeerStyleEnumProvided() throws Exception {
        val response = mockMvc.perform(get(BASE_URL)
                        .queryParam("beerStyle", "XXX"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("Failed to convert");
    }

    @Test
    void shouldReturnNotFoundStatusWhenGetBeerByIdNotFound() throws Exception {
        when(beerService.getBeerbyId(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()))
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

        val result = mockMvc.perform(post(BASE_URL)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer)));

        result.andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldFailNewBeerCreationIfBeerNameIsEmpty() throws Exception {
        val beer = BeerDto.builder().build();

        when(beerService.saveNewBeer(any(BeerDto.class))).thenReturn(beer);

        val result = mockMvc.perform(post(BASE_URL)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer)));

        result.andExpect(jsonPath("$.length()", is(4)))
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

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(id, beer);
    }

    @Test
    void shouldFailIfUpdateExistingBeerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder().build();
        when(beerService.updateBeerById(id, beer)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());

        verify(beerService).updateBeerById(id, beer);
    }

    static Stream<Arguments> shouldPatchExistingBeerById() {
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
    void shouldPatchExistingBeerById(UUID id, BeerDto beer) throws Exception {
        when(beerService.patchBeerById(id, beer)).thenReturn(Optional.of(beer));

        mockMvc.perform(patch(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(idCaptor.capture(), beerCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(beerCaptor.getValue()).isEqualTo(beer);
    }

    @Test
    void shouldFailIfPatchExistingBeerByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder().build();
        when(beerService.updateBeerById(id, beer)).thenReturn(Optional.empty());

        mockMvc.perform(patch(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());

        verify(beerService).patchBeerById(id, beer);
    }

    @Test
    void shouldDeleteExistingBeerById() throws Exception {
        val id = UUID.randomUUID();

        when(beerService.deleteBeerById(id)).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteBeerById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
    }

    @Test
    void shouldFailIfDeleteByIdIsNotFound() throws Exception {
        val id = UUID.randomUUID();

        when(beerService.deleteBeerById(id)).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/" + id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}