package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.bootstrap.BootstrapData;
import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static dev.cadebe.spring6restmvc.model.BeerStyle.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    @Transactional
    void shouldSaveNewBeer() {
        val savedBeer = beerRepository.save(BeerEntity.builder()
                .beerName("Some name")
                .beerStyle(BeerStyle.PILSNER)
                .upc("12345")
                .price(new BigDecimal("1.99"))
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void shouldFailWhenBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(BeerEntity.builder()
                    .beerName("012345678901234567890123456789012345678901234567890")
                    .beerStyle(BeerStyle.PILSNER)
                    .upc("12345")
                    .price(new BigDecimal("1.99"))
                    .build());

            beerRepository.flush();
        });
    }

    @Test
    void shouldFindAllBeersByBeerName() {
        val beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");

        assertThat(beerList).hasSize(336);
        assertThat(beerList.subList(0, 3))
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getUpc)
                .containsExactly(
                        tuple("21st Amendment IPA (2006)", IPA, "29"),
                        tuple("Brew Free! or Die IPA (2008)", IPA, "30"),
                        tuple("Brew Free! or Die IPA (2009)", IPA, "31"));
    }

    @Test
    void shouldFindAllBeersByBeerStyle() {
        val beerList = beerRepository.findAllByBeerStyle(PORTER);

        assertThat(beerList).hasSize(74);
        assertThat(beerList.subList(0, 3))
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getUpc)
                .containsExactly(
                        tuple("Foreman", PORTER, "8"),
                        tuple("He Said Baltic-Style Porter", PORTER, "20"),
                        tuple("Porter (a/k/a Black Gold Porter)", PORTER, "131"));
    }

    @Test
    void shouldFindAllBeersByBeerNameAndBeerStyle() {
        val beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%american%", ALE);

        assertThat(beerList).hasSize(26);
        assertThat(beerList.subList(0, 3))
                .extracting(BeerEntity::getBeerName, BeerEntity::getBeerStyle, BeerEntity::getUpc)
                .containsExactly(
                        tuple("Bitter American", ALE, "25"),
                        tuple("Bitter American (2011)", ALE, "34"),
                        tuple("Banner American Rye", ALE, "190"));
    }
}