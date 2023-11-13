package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.config.BeerServiceProperties;
import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.mappers.BeerMapper;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BeerServiceJpaIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    BeerServiceJpa beerService;

    static BeerServiceProperties beerServiceProperties = new BeerServiceProperties();

    static Stream<Arguments> getAllBeersByVaryingQueryParameters() {
        return Stream.of(
                Arguments.of(new BeerParameters(null, null, null, null, null), beerServiceProperties.getDefaultPageSize(), 97),
                Arguments.of(new BeerParameters("American", null, true, 1, null), 37, 1),
                Arguments.of(new BeerParameters(null, BeerStyle.IPA, true, 1, null), 548, 1),
                Arguments.of(new BeerParameters("ALE", BeerStyle.ALE, true, 1, null), 537, 1),
                Arguments.of(new BeerParameters("ALE", BeerStyle.ALE, false, 5, 20), 537, 1),
                Arguments.of(new BeerParameters(null, null, true, 5, 20), 20, 121),
                Arguments.of(new BeerParameters(null, BeerStyle.LAGER, true, 500, beerServiceProperties.getDefaultPageSize()), 105, 1),
                Arguments.of(new BeerParameters(null, BeerStyle.LAGER, false, 500, 1001), 105, 1));
    }

    @ParameterizedTest
    @MethodSource
    void getAllBeersByVaryingQueryParameters(BeerParameters parameters, int contentSize, int totalPages) {
        val beerPage = beerService.listBeers(parameters.beerName(), parameters.beerStyle(), parameters.showInventory(), parameters.pageNumber(), parameters.pageSize());

        assertThat(beerPage.getContent()).hasSize(contentSize);
        assertThat(beerPage.getTotalPages()).isEqualTo(totalPages);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListIfNoBeersPresent() {
        beerRepository.deleteAll();

        val result = beerService.listBeers(null, null, null, null, null);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalPages()).isZero();
    }

    @Test
    void shouldFindBeerById() {
        val first = beerRepository.findAll().get(0);
        val result = beerService.getBeerbyId(first.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenBeerCannotBeFoundById() {
        val result = beerService.getBeerbyId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void shouldSaveNewBeer() {
        val beersBefore = beerRepository.findAll();

        val beer = BeerDto.builder()
                .beerName("Some new beer name")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("1234567890")
                .price(new BigDecimal("14.49"))
                .quantityOnHand(23)
                .build();

        val start = LocalDateTime.now();
        val savedBeer = beerService.saveNewBeer(beer);
        val finish = LocalDateTime.now();

        val beersAfter = beerRepository.findAll();

        assertThat(beersAfter.size() - beersBefore.size()).isEqualTo(1);
        assertThat(savedBeer)
                .extracting(BeerDto::getBeerName, BeerDto::getBeerStyle, BeerDto::getUpc, BeerDto::getPrice, BeerDto::getQuantityOnHand)
                .containsExactly("Some new beer name", BeerStyle.PALE_ALE, "1234567890", new BigDecimal("14.49"), 23);

        // TODO
        // beerRepository.flush();
        // assertThat(savedBeer.getCreatedDate()).isBetween(start, finish);
        // assertThat(savedBeer.getUpdatedDate()).isBetween(start, finish);
        // assertThat(savedBeer.getVersion()).isEqualTo(1);
    }

    static Stream<BeerDto> shouldUpdateExistingBeerById() {
        return Stream.of(
                BeerDto.builder()
                        .beerName("Beer XZY")
                        .build(),

                BeerDto.builder()
                        .beerName("Beer ABC")
                        .beerStyle(BeerStyle.PORTER)
                        .build(),

                BeerDto.builder()
                        .beerName("Beer DEF")
                        .beerStyle(BeerStyle.IPA)
                        .upc("qwerty")
                        .build(),

                BeerDto.builder()
                        .beerName("Beer GHI")
                        .beerStyle(BeerStyle.BITTER)
                        .upc("qwertz")
                        .price(new BigDecimal("15.49"))
                        .build(),

                BeerDto.builder()
                        .beerName("Beer JKL")
                        .beerStyle(BeerStyle.SAISON)
                        .upc("asdfgh")
                        .price(new BigDecimal("11.79"))
                        .build());
    }

    @ParameterizedTest
    @MethodSource
    @Transactional
    @Rollback
    void shouldUpdateExistingBeerById(BeerDto beerDto) {
        val first = beerRepository.findAll().get(0);

        assertThat(first)
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getPrice, BeerEntity::getUpc, BeerEntity::getQuantityOnHand)
                .containsExactly("Galaxy Cat", BeerStyle.PALE_ALE, new BigDecimal("12.99"), "12356", 122);

        val existing = beerMapper.toModel(first);
        existing.setBeerName(beerDto.getBeerName());
        existing.setBeerStyle(beerDto.getBeerStyle());
        existing.setUpc(beerDto.getUpc());
        existing.setPrice(beerDto.getPrice());
        existing.setQuantityOnHand(beerDto.getQuantityOnHand());

        val result = beerService.updateBeerById(first.getId(), existing);

        assertThat(result).isNotEmpty();
        assertThat(result.get())
                .extracting(BeerDto::getBeerName, BeerDto::getBeerStyle, BeerDto::getPrice, BeerDto::getUpc, BeerDto::getQuantityOnHand)
                .containsExactly(beerDto.getBeerName(), beerDto.getBeerStyle(), beerDto.getPrice(), beerDto.getUpc(), beerDto.getQuantityOnHand());
    }

    @Test
    @Transactional
    void shouldNotUpdateBeerWhenNotFound() {
        val beer = BeerDto.builder()
                .beerName("Some new beer name")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("1234567890")
                .price(new BigDecimal("14.49"))
                .quantityOnHand(23)
                .build();

        val result = beerService.updateBeerById(UUID.randomUUID(), beer);

        assertThat(result).isEmpty();
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

        beerService.patchBeerById(id, beerDto);

        val patchedBeer = beerRepository.findById(id);

        assertThat(patchedBeer.get())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(beerMapper.toEntity(beerDto));
    }

    @Test
    @Transactional
    void shouldNotPatchBeerWhenNotFound() {
        val id = UUID.randomUUID();
        val beerDto = BeerDto.builder()
                .beerName("Some new name")
                .beerStyle(BeerStyle.CIDER)
                .price(new BigDecimal("9.78"))
                .quantityOnHand(9999)
                .upc("abc123")
                .build();

        val existing = beerRepository.findById(id);

        val result = beerService.patchBeerById(id, beerDto);

        assertThat(existing).isEmpty();
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void shouldDeleteBeerById() {
        val first = beerRepository.findAll().get(0);

        beerService.deleteBeerById(first.getId());

        assertThat(beerRepository.findById(first.getId())).isEmpty();
    }

    @Test
    @Transactional
    void shouldNotDeleteBeerWhereBeerIsNotFound() {
        val id = UUID.randomUUID();

        beerService.deleteBeerById(id);

        assertThat(beerRepository.findById(id)).isEmpty();
    }

    record BeerParameters(String beerName,
                          BeerStyle beerStyle,
                          Boolean showInventory,
                          Integer pageNumber,
                          Integer pageSize) {
    }
}
