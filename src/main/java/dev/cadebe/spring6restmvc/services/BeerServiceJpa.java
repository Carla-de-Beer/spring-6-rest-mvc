package dev.cadebe.spring6restmvc.services;

import dev.cadebe.spring6restmvc.config.BeerServiceProperties;
import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.mappers.BeerMapper;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.repositories.BeerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.val;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
public class BeerServiceJpa implements BeerService {

    private final BeerServiceProperties beerServiceProperties;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private Counter objectsCount;

    public BeerServiceJpa(BeerServiceProperties beerServiceProperties, BeerRepository beerRepository, BeerMapper beerMapper, MeterRegistry meterRegistry) {
        this.beerServiceProperties = beerServiceProperties;
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;

        objectsCount = Counter.builder("beer.object.count")
                .tag("addition", "manual")
                .register(meterRegistry);
    }

    @Override
    public Page<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        val pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<BeerEntity> beerPage;

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerPage = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", null);
        } else if (beerStyle != null && !StringUtils.hasText(beerName)) {
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, null);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerPage = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, null);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if (showInventory != null && !showInventory) {
            beerPage.forEach(beerEntity -> beerEntity.setQuantityOnHand(null));
        }

        return beerPage.map(beerMapper::toModel);
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = beerServiceProperties.getDefaultPageSize();
        }

        if (pageSize == null) {
            queryPageSize = beerServiceProperties.getDefaultPageSize();
        } else {
            if (pageSize > beerServiceProperties.getPageLimit()) {
                queryPageSize = beerServiceProperties.getPageLimit();
            } else {
                queryPageSize = pageSize;
            }
        }

        val sort = Sort.by(Sort.Order.asc("beerName"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    @Override
    public Optional<BeerDto> getBeerbyId(UUID id) {
        return Optional.ofNullable(beerMapper.toModel(beerRepository.findById(id).orElse(null)));
    }

    @Override
    @Transactional
    public BeerDto saveNewBeer(BeerDto beer) {
        objectsCount.increment();
        return beerMapper.toModel(beerRepository.save(beerMapper.toEntity(beer)));
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public Optional<BeerDto> patchBeerById(UUID beerId, BeerDto beer) {
        AtomicReference<Optional<BeerDto>> beerReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            if (StringUtils.hasText(beer.getBeerName())) {
                foundBeer.setBeerName(beer.getBeerName());
            }

            if (beer.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beer.getBeerStyle());
            }

            if (StringUtils.hasText(beer.getUpc())) {
                foundBeer.setUpc(beer.getUpc());
            }

            if (beer.getPrice() != null) {
                foundBeer.setPrice(beer.getPrice());
            }

            if (beer.getQuantityOnHand() != null) {
                foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }

            if (StringUtils.hasText(beer.getUpc())) {
                foundBeer.setUpc(beer.getUpc());
            }

            beerRepository.save(foundBeer);
            beerReference.set(Optional.of(beerMapper.toModel(foundBeer)));

        }, () -> beerReference.set(Optional.empty()));

        return beerReference.get();
    }

    @Override
    @Transactional
    public boolean deleteBeerById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }

        return false;
    }
}
