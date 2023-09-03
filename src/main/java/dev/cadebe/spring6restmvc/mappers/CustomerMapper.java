package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "beerOrders", ignore = true)
    CustomerEntity toEntity(CustomerDto customerDto);

    CustomerDto toModel(CustomerEntity customerEntity);
}
