package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.model.CustomerDto;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    CustomerMapper customerMapper = new CustomerMapperImpl();

    @Test
    void shouldMapFromDtoToEntity() {
        val id = UUID.randomUUID();
        val version = 5;
        val name = "Customer XZY";
        val email = "123@abc.com";
        val created = LocalDateTime.parse("2023-08-24T15:48:00.00");
        val updated = LocalDateTime.parse("2023-08-25T17:32:00.00");

        val customerDto = CustomerDto.builder()
                .id(id)
                .version(version)
                .name(name)
                .email(email)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        val mapped = customerMapper.toEntity(customerDto);

        val expected = CustomerEntity.builder()
                .id(id)
                .version(version)
                .name(name)
                .email(email)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        assertThat(mapped).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldMapFromEntityToDto() {
        val id = UUID.randomUUID();
        val version = 7;
        val name = "Customer QWR";
        val email = "456@def.com";
        val created = LocalDateTime.parse("2023-08-26T19:17:00.00");
        val updated = LocalDateTime.parse("2023-08-27T07:22:00.00");

        val customerEntity = CustomerEntity.builder()
                .id(id)
                .version(version)
                .name(name)
                .email(email)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        val mapped = customerMapper.toModel(customerEntity);

        val expected = CustomerDto.builder()
                .id(id)
                .version(version)
                .name(name)
                .email(email)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        assertThat(mapped).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldMapFromDtoToEntityForNullInput() {
        val mapped = customerMapper.toEntity(null);

        assertThat(mapped).isNull();
    }

    @Test
    void shouldMapFromEntityToDtoForNullInput() {
        val mapped = customerMapper.toEntity(null);

        assertThat(mapped).isNull();
    }
}