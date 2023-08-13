package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerEntity toEntity(CustomerDto customerDto);

    CustomerDto toModel(CustomerEntity customerEntity);
}
