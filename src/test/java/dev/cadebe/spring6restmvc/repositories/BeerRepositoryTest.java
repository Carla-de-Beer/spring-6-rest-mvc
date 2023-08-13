package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;


    @Test
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
            val savedBeer = beerRepository.save(BeerEntity.builder()
                    .beerName("012345678901234567890123456789012345678901234567890")
                    .beerStyle(BeerStyle.PILSNER)
                    .upc("12345")
                    .price(new BigDecimal("1.99"))
                    .build());

            beerRepository.flush();
        });
    }
}