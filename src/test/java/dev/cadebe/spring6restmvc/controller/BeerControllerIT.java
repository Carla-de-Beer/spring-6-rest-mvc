package dev.cadebe.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.data.BeerOrderLineEntity;
import dev.cadebe.spring6restmvc.mappers.BeerMapper;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import lombok.val;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void shouldGetBeerList() {
        val beers = beerController.getBeers(null, null, false, 1, 1001);

        assertThat(beers).hasSize(1000);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListIfNoBeersFound() {
        beerRepository.deleteAll();
        val beers = beerController.getBeers(null, null, false, 1, 25);

        assertThat(beers).isEmpty();
    }

    @Test
    void shouldGetBeerById() {
        val beer = beerRepository.findAll().get(0);
        val found = beerController.getBeerById(beerMapper.toModel(beer).getId());

        assertThat(found).isNotNull();
    }

    @Test
    void shouldGetBeerByNameWithoutPaging() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%IPA%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(336)))
                .andExpect(jsonPath("$.content.[0].beerName", is("21st Amendment IPA (2006)")))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByNameWithPaging() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%IPA%")
                        .queryParam("pageNumber", "3")
                        .queryParam("pageSize", "56"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(336)))
                .andExpect(jsonPath("$.content.[0].beerName", is("21st Amendment IPA (2006)")))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByBeerStyleWithoutPaging() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerStyle", "PORTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(74)))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.PORTER.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByBeerStyleWithPaging() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerStyle", "PORTER")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(74)))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.PORTER.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByBeerNameAndBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%american%")
                        .queryParam("beerStyle", "ALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(26)))
                .andExpect(jsonPath("$.content.[0].beerName", is("Bitter American")))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.ALE.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByBeerNameAndBeerStyleAndNotShowInventoryFalse() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%american%")
                        .queryParam("beerStyle", "ALE")
                        .queryParam("showInventory", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(26)))
                .andExpect(jsonPath("$.content.[0].beerName", is("Bitter American")))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.ALE.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void shouldGetBeerByBeerNameAndBeerStyleAndNotShowInventoryTruePage1() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%american%")
                        .queryParam("beerStyle", BeerStyle.ALE.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(26)))
                .andExpect(jsonPath("$.content.[0].beerName", is("Bitter American")))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.ALE.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldGetBeerByBeerNameAndBeerStyleAndShowInventoryTrue() throws Exception {
        mockMvc.perform(get(BeerController.BASE_URL)
                        .queryParam("beerName", "%american%")
                        .queryParam("beerStyle", "ALE")
                        .queryParam("showInventory", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(26)))
                .andExpect(jsonPath("$.content.[0].beerName", is("Bitter American")))
                .andExpect(jsonPath("$.content.[0].beerStyle", is(BeerStyle.ALE.name())))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void shouldFailIfGetBeerByIdReturnsEmpty() {
        val id = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> beerController.getBeerById(id));
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveNewBeer() {
        val name = "New Beer";
        assertThat(beerRepository.findAll()).hasSize(2413);

        val beer = BeerDto.builder()
                .beerName(name)
                .beerStyle(BeerStyle.PORTER)
                .upc("12345")
                .price(new BigDecimal("9.99"))
                .build();

        val result = beerController.saveNewBeer(beer);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getHeaders().getLocation()).isNotNull();

        val pathSegments = result.getHeaders().getLocation().getPath().split("/");
        val savedBeer = beerRepository.findById(UUID.fromString(pathSegments[4]));

        assertThat(savedBeer.get()).isNotNull();
        assertThat(savedBeer.get())
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getPrice)
                .containsExactly(name, BeerStyle.PORTER, new BigDecimal("9.99"));
        assertThat(beerRepository.findAll()).hasSize(2414);
    }

    @Test
    void shouldFailDataValiodationForJpaConstraintsAtWebTierLevel() throws Exception {
        val beer = BeerEntity.builder()
                .beerName("012345678901234567890123456789012345678901234567890")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12345")
                .price(new BigDecimal("1.99"))
                .build();

        mockMvc.perform(post(BeerController.BASE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateExistingBeer() {
        val beer = beerRepository.findAll().get(0);
        val id = beer.getId();
        val beerDto = beerMapper.toModel(beer);
        beerDto.setBeerName("UPDATED");

        val result = beerController.updateById(id, beerDto);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        val updatedBeer = beerRepository.findById(id);

        assertThat(updatedBeer.get())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(beerMapper.toEntity(beerDto));
    }

    @Test
    @Transactional
    @Rollback
    void shouldFailUpdateByIdIfBeerNotFound() {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder().build();

        assertThrows(NotFoundException.class, () -> beerController.updateById(id, beer));
    }

    @Test
    @Transactional
    @Rollback
    void shouldPatchExistingBeer() {
        val beer = beerRepository.findAll().get(0);
        val id = beer.getId();
        val beerDto = beerMapper.toModel(beer);

        assertThat(beer)
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getPrice, BeerEntity::getQuantityOnHand, BeerEntity::getUpc)
                .containsExactly("Galaxy Cat", BeerStyle.PALE_ALE, new BigDecimal("12.99"), 122, "12356");

        beerDto.setBeerName("Some new name");
        beerDto.setBeerStyle(BeerStyle.CIDER);
        beerDto.setPrice(new BigDecimal("9.78"));
        beerDto.setQuantityOnHand(9999);
        beerDto.setUpc("abc123");

        val result = beerController.patchBeerById(id, beerDto);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        val patchedBeer = beerRepository.findById(id);

        assertThat(patchedBeer.get())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(beerMapper.toEntity(beerDto));
    }

    @Test
    void shouldFailPatchByIdIfBeerNotFound() {
        val id = UUID.randomUUID();
        val beer = BeerDto.builder().build();

        assertThrows(NotFoundException.class, () -> beerController.patchBeerById(id, beer));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteBeerById() {
        val beer = beerRepository.findAll().get(0);

        assertThat(beerRepository.findById(beer.getId()).get()).isNotNull();

        var result = beerController.deleteBeerById(beer.getId());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void shouldFailDeleteByIdIfBeerNotFound() {
        val id = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> beerController.deleteBeerById(id));
    }
}