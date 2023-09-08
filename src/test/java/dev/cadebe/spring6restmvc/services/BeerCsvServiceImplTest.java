package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.BeerCsv;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerCsvServiceImplTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void shouldCreateListOfBeerCsvObjectsFromCsvFile() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("csvdata/beers.csv");
        InputStream inputStream = classPathResource.getInputStream();

        val expectedFirst = new BeerCsv();
        expectedFirst.setRow("1");
        expectedFirst.setCountX(1);
        expectedFirst.setAbv("0.05");
        expectedFirst.setIbu("NA");
        expectedFirst.setId("1436");
        expectedFirst.setBeer("Pub Beer");
        expectedFirst.setStyle("American Pale Lager");
        expectedFirst.setBreweryId(408);
        expectedFirst.setOunces(12f);
        expectedFirst.setStyle2("NA");
        expectedFirst.setCountY(409);
        expectedFirst.setBrewery("10 Barrel Brewing Company");
        expectedFirst.setCity("Bend");
        expectedFirst.setState("OR");
        expectedFirst.setLabel("Pub Beer (10 Barrel Brewing Company)");

        val expectedLast = new BeerCsv();
        expectedLast.setRow("2410");
        expectedLast.setCountX(2410);
        expectedLast.setAbv("0.052");
        expectedLast.setIbu("NA");
        expectedLast.setId("84");
        expectedLast.setBeer("Rail Yard Ale (2009)");
        expectedLast.setStyle("American Amber / Red Ale");
        expectedLast.setBreweryId(424);
        expectedLast.setOunces(12f);
        expectedLast.setStyle2("American Amber / Red Ale");
        expectedLast.setCountY(425);
        expectedLast.setBrewery("Wynkoop Brewing Company");
        expectedLast.setCity("Denver");
        expectedLast.setState("CO");
        expectedLast.setLabel("Rail Yard Ale (2009) (Wynkoop Brewing Company)");

        val beerList = beerCsvService.convertCsv(new InputStreamReader(inputStream));

        assertThat(beerList).hasSize(2410);

        assertThat(beerList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(expectedFirst);

        assertThat(beerList.get(beerList.size() - 1))
                .usingRecursiveComparison()
                .isEqualTo(expectedLast);
    }

    @Test
    void shouldFailWhenFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            ClassPathResource classPathResource = new ClassPathResource("csvdata/missing-file.csv");
            InputStream inputStream = classPathResource.getInputStream();
            beerCsvService.convertCsv(new InputStreamReader(inputStream));
        });
    }
}