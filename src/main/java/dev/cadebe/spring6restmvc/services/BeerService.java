package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory);

    Optional<BeerDto> getBeerbyId(UUID id);

    BeerDto saveNewBeer(BeerDto beer);

    Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beer);

    void patchBeerById(UUID beerId, BeerDto beer);

    boolean deleteBeerById(UUID beerId);
}
