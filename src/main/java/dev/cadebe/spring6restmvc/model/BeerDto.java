package dev.cadebe.spring6restmvc.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class BeerDto {

    private UUID id;

    private Integer version;

    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String beerName;

    private BeerStyle beerStyle;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String upc;

    private BigDecimal price;

    private Integer quantityOnHand;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    public BeerDto() {
        id = UUID.randomUUID();
        version = 0;
        beerName = "";
        beerStyle = BeerStyle.ALE;
        upc = "";
        price = BigDecimal.ZERO;
        quantityOnHand = 0;
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
}
