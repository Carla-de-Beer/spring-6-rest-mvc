package dev.cadebe.spring6restmvc.bootstrap;

import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import dev.cadebe.spring6restmvc.services.BeerCsvService;
import dev.cadebe.spring6restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
class BootstrapDataTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerCsvService beerCsvService;

    @Autowired
    CustomerRepository customerRepository;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, beerCsvService, customerRepository);
    }

    @Test
    void shouldPersistNewBeerEntriesToTheDatabase() throws Exception {
        bootstrapData.run("");

        assertThat(beerRepository.count()).isEqualTo(2413);
        assertThat(customerRepository.count()).isEqualTo(3);
    }
}