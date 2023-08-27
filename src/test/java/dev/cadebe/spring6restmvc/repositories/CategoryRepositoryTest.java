package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.bootstrap.BootstrapData;
import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.data.CategoryEntity;
import dev.cadebe.spring6restmvc.services.BeerCsvServiceImpl;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class CategoryRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    BeerEntity testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }

    @Test
    void shouldAddNewCategory() {
        val description = "Some category description";

        val savedCategory = categoryRepository.save(
                CategoryEntity.builder()
                        .description(description)
                        .build()
        );

        savedCategory.getBeers().add(testBeer);
        testBeer.addCategory(savedCategory);

        val savedBeer = beerRepository.save(testBeer);

        assertThat(savedCategory.getBeers()).isNotNull();
        assertThat(savedCategory.getBeers())
                .extracting(BeerEntity::getBeerName)
                .containsExactly("Galaxy Cat");

        assertThat(savedBeer.getCategories()).isNotNull();
        assertThat(savedBeer.getCategories())
                .extracting(CategoryEntity::getDescription)
                .containsExactly(description);
    }
}