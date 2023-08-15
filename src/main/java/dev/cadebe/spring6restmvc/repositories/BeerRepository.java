package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<BeerEntity, UUID> {

    Page<BeerEntity> findAllByBeerNameIsLikeIgnoreCase(String beerName, Pageable pageable);

    Page<BeerEntity> findAllByBeerStyle(BeerStyle beerStyle, Pageable pa1);

    Page<BeerEntity> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle, Pageable pageable);

}