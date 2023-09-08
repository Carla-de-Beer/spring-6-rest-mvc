package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.BeerCsv;

import java.io.Reader;
import java.util.List;

public interface BeerCsvService {

    List<BeerCsv> convertCsv(Reader csvReader);
}
