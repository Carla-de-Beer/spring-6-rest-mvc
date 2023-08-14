package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.mappers.BeerMapper;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {
        List<BeerEntity> beerList;

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
        } else if (beerStyle != null && !StringUtils.hasText(beerName)) {
            beerList = beerRepository.findAllByBeerStyle(beerStyle);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }

        if (showInventory != null && !showInventory) {
            beerList.forEach(beerEntity -> beerEntity.setQuantityOnHand(null));
        }

        return beerList.stream()
                .map(beerMapper::toModel)
                .toList();
    }

    @Override
    public Optional<BeerDto> getBeerbyId(UUID id) {
        return Optional.ofNullable(beerMapper.toModel(beerRepository.findById(id).orElse(null)));
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beer) {
        return beerMapper.toModel(beerRepository.save(beerMapper.toEntity(beer)));
    }

    @Override
    public Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beer) {
        AtomicReference<Optional<BeerDto>> beerReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setPrice(beer.getPrice());
            foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            beerRepository.save(foundBeer);
            beerReference.set(Optional.of(beerMapper.toModel(foundBeer)));
        }, () -> beerReference.set(Optional.empty()));

        return beerReference.get();
    }

    // TODO
    @Override
    public void patchBeerById(UUID beerId, BeerDto beer) {

    }

    @Override
    public boolean deleteBeerById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }

        return false;
    }
}
