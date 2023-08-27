package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.mappers.BeerMapper;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static dev.cadebe.spring6restmvc.services.BeerServiceJpa.DEFAULT_PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeerServiceJpaIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    BeerServiceJpa beerService;

    @Test
    void name() {
        val beerPage = beerService.listBeers(null, null, null, null, null);

        assertThat(beerPage.getContent()).hasSize(DEFAULT_PAGE_SIZE);
        assertThat(beerPage.getTotalPages()).isEqualTo(97);
    }

    @Test
    void name2() {
        val beerPage = beerService.listBeers("ALE", BeerStyle.ALE, true, 1, DEFAULT_PAGE_SIZE);

        // assertThat(beerPage.getContent()).hasSize(DEFAULT_PAGE_SIZE);

        assertThat(beerPage.getContent()).hasSize(537);
        assertThat(beerPage.getTotalPages()).isEqualTo(1);
    }
}