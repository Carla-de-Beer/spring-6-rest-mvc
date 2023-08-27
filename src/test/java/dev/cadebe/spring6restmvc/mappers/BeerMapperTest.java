package dev.cadebe.spring6restmvc.mappers;

import dev.cadebe.spring6restmvc.data.*;
import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerMapperTest {

    BeerMapper beerMapper = new BeerMapperImpl();

    @Test
    void shouldMapEntityToDto() {
        val beerId = UUID.randomUUID();
        val beerOrderId = UUID.randomUUID();
        val beerOrderLineId = UUID.randomUUID();

        val beerOrderLine =
                BeerOrderLineEntity.builder()
                        .id(beerOrderLineId)
                        .version(2L)
                        .orderQuantity(122)
                        .quantityAllocated(34)
                        .createdDate(LocalDateTime.parse("2023-08-20T12:10:00.00"))
                        .updatedDate(LocalDateTime.parse("2023-08-21T14:20:00.00"))
                        .beerOrder(BeerOrderEntity.builder()
                                .id(beerOrderId)
                                .version(3L)
                                .customerRef("some customer ref")
                                .customer(CustomerEntity.builder().build())
                                .createdDate(LocalDateTime.parse("2023-08-18T08:15:00.00"))
                                .updatedDate(LocalDateTime.parse("2023-08-19T09:25:00.00"))
                                .beerOrderShipment(BeerOrderShipmentEntity.builder()
                                        .id(UUID.randomUUID())
                                        .version(3L)
                                        .trackingNumber("TR-09876")
                                        .createdDate(LocalDateTime.parse("2023-08-16T12:10:00.00"))
                                        .updatedDate(LocalDateTime.parse("2023-08-17T14:20:00.00"))
                                        .beerOrder(BeerOrderEntity.builder()
                                                .id(UUID.randomUUID())
                                                .customer(CustomerEntity.builder()
                                                        .version(3)
                                                        .name("Some customer")
                                                        .email("1234@abc.com")
                                                        .build())
                                                .createdDate(LocalDateTime.parse("2023-08-18T10:15:00.00"))
                                                .updatedDate(LocalDateTime.parse("2023-08-19T11:25:00.00"))
                                                .beerOrderShipment(BeerOrderShipmentEntity.builder()
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .beer(BeerEntity.builder()
                                .beerName("Some beer name")
                                .beerStyle(BeerStyle.PILSNER)
                                .createdDate(LocalDateTime.parse("2023-08-19T12:18:20.00"))
                                .updatedDate(LocalDateTime.parse("2023-08-18T14:19:30.00"))
                                .build())
                        .build();

        val beerEntity = BeerEntity.builder()
                .id(beerId)
                .version(1)
                .beerName("Some beer name")
                .beerStyle(BeerStyle.PILSNER)
                .upc("abc123")
                .quantityOnHand(42)
                .price(BigDecimal.TEN)
                .createdDate(LocalDateTime.parse("2023-08-19T12:18:20.00"))
                .updatedDate(LocalDateTime.parse("2023-08-18T14:19:30.00"))
                .beerOrderLines(Set.of(beerOrderLine))
                .categories(Set.of())
                .build();

        val mapped = beerMapper.toModel(beerEntity);

        val expected = BeerDto.builder()
                .id(beerId)
                .version(1)
                .beerName("Some beer name")
                .beerStyle(BeerStyle.PILSNER)
                .upc("abc123")
                .quantityOnHand(42)
                .price(BigDecimal.TEN)
                .createdDate(LocalDateTime.parse("2023-08-19T12:18:20.00"))
                .updatedDate(LocalDateTime.parse("2023-08-18T14:19:30.00"))
                .build();

        assertThat(mapped).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldMapFromDtoToEntity() {
        val id = UUID.randomUUID();

        val beerDto = BeerDto.builder()
                .id(id)
                .version(4)
                .beerName("Some beer name")
                .beerStyle(BeerStyle.PORTER)
                .upc("asdfer")
                .price(BigDecimal.TEN)
                .quantityOnHand(27)
                .createdDate(LocalDateTime.parse("2023-08-19T12:18:20.00"))
                .updatedDate(LocalDateTime.parse("2023-08-18T14:19:30.00"))
                .build();

        val mapped = beerMapper.toEntity(beerDto);

        val expected = BeerEntity.builder()
                .id(id)
                .id(id)
                .version(4)
                .beerName("Some beer name")
                .beerStyle(BeerStyle.PORTER)
                .upc("asdfer")
                .price(BigDecimal.TEN)
                .quantityOnHand(27)
                .createdDate(LocalDateTime.parse("2023-08-19T12:18:20.00"))
                .updatedDate(LocalDateTime.parse("2023-08-18T14:19:30.00"))
                .beerOrderLines(Collections.EMPTY_SET)
                .categories(Collections.EMPTY_SET)
                .build();

        assertThat(mapped).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldMapFromDtoToEntityForNullInput() {
        val mapped = beerMapper.toEntity(null);

        assertThat(mapped).isNull();
    }

    @Test
    void shouldMapFromEntityToDtoForNullInput() {
        val mapped = beerMapper.toEntity(null);

        assertThat(mapped).isNull();
    }
}