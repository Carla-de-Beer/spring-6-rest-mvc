package dev.cadebe.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerDto {

    private UUID id;

    private Integer version;

    private String name;

    private String email;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}