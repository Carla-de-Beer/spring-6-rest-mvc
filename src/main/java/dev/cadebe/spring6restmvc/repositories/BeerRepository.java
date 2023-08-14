package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeerRepository extends JpaRepository<BeerEntity, UUID> {

    List<BeerEntity> findAllByBeerNameIsLikeIgnoreCase(String beerName);

    List<BeerEntity> findAllByBeerStyle(BeerStyle beerStyle);

    List<BeerEntity> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle);

}