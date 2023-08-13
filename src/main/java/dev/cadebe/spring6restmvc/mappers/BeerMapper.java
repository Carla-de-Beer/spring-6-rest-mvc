package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import dev.cadebe.spring6restmvc.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    BeerEntity toEntity(BeerDto beerDto);

    BeerDto toModel(BeerEntity beerEntity);
}
