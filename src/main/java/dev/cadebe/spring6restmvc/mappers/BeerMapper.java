package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerMapper {

    @Mapping(target = "beerOrderLines", ignore = true)
    @Mapping(target = "categories", ignore = true)
    BeerEntity toEntity(BeerDto beerDto);

    BeerDto toModel(BeerEntity beerEntity);
}
