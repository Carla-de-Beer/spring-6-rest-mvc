package dev.cadebe.spring6restmvc.bootstrap;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.model.BeerCsv;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import dev.cadebe.spring6restmvc.repositories.CustomerRepository;
import dev.cadebe.spring6restmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static dev.cadebe.spring6restmvc.model.BeerStyle.*;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final BeerCsvService beerCsvService;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public void run(String... args) {
        loadBeerData();
        loadCsvData();
        loadCustomerData();
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            val beer1 = BeerEntity.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(PALE_ALE)
                    .upc("12356")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .build();

            val beer2 = BeerEntity.builder()
                    .beerName("Crank")
                    .beerStyle(PALE_ALE)
                    .upc("12356222")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .build();

            val beer3 = BeerEntity.builder()
                    .beerName("Sunshine City")
                    .beerStyle(IPA)
                    .upc("12356")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .build();

            beerRepository.saveAll(List.of(beer1, beer2, beer3));
        }
    }

    private void loadCsvData() {
        if (beerRepository.count() < 10) {
            try {
                var file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
                var beerCsvList = beerCsvService.convertCsv(file);

                val beers = beerCsvList.stream()
                        .map(beerCsv -> BeerEntity.builder()
                                .beerName(StringUtils.abbreviate(beerCsv.getBeer(), 50))
                                .beerStyle(getBeerStyle(beerCsv))
                                .upc(beerCsv.getRow())
                                .price(BigDecimal.TEN)
                                .quantityOnHand(beerCsv.getCountX())
                                .build())
                        .toList();

                beerRepository.saveAll(beers);

            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Csv file could not be found", e);
            }
        }
    }

    private static BeerStyle getBeerStyle(BeerCsv beerCsv) {
        return switch (beerCsv.getStyle()) {
            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> IPA;
            case "Oatmeal Stout", "American Stout", "Milk / Sweet Stout", "Schwarzbier" -> STOUT;
            case "American Porter", "Baltic Porter" -> PORTER;
            case "Saison / Farmhouse Ale" -> SAISON;
            case "Cider" -> CIDER;
            case "Fruit / Vegetable Beer", "Berliner Weissbier", "Altbier", "Winter Warmer", "Hefeweizen", "Rauchbier" ->
                    WHEAT;
            case "German Pilsener", "Czech Pilsener" -> PILSNER;
            case "American Pale Lager", "Vienna Lager", "Euro Pale Lager", "Munich Helles Lager", "Dortmunder / Export Lager", "American Adjunct Lager" ->
                    LAGER;
            case "Extra Special / Strong Bitter (ESB)" -> BITTER;
            default -> ALE;
        };
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            val customer1 = CustomerEntity.builder()
                    .name("Customer 1")
                    .email("123@abc.com")
                    .build();

            val customer2 = CustomerEntity.builder()
                    .name("Customer 2")
                    .email("456@def.com")
                    .build();

            val customer3 = CustomerEntity.builder()
                    .name("Customer 3")
                    .email("789@ghi.com")
                    .build();

            customerRepository.saveAll(List.of(customer1, customer2, customer3));
        }
    }
}
