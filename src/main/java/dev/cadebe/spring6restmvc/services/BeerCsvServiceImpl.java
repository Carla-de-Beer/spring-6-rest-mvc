package dev.cadebe.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.cadebe.spring6restmvc.model.BeerCsv;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    public List<BeerCsv> convertCsv(Reader csvReader) {
        return new CsvToBeanBuilder<BeerCsv>(csvReader)
                .withType(BeerCsv.class)
                .build()
                .parse();
    }
}
