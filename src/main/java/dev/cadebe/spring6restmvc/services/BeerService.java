package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Page<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);

    Optional<BeerDto> getBeerbyId(UUID id);

    BeerDto saveNewBeer(BeerDto beer);

    Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beer);

    void patchBeerById(UUID beerId, BeerDto beer);

    boolean deleteBeerById(UUID beerId);
}
