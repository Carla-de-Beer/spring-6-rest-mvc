package dev.cadebe.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.cadebe.spring6restmvc.model.BeerCsv;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    public List<BeerCsv> convertCsv(File csvFile) {

        try {
            return new CsvToBeanBuilder<BeerCsv>(new FileReader(csvFile))
                    .withType(BeerCsv.class)
                    .build()
                    .parse();

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Csv file could not be found", e);
        }
    }
}
