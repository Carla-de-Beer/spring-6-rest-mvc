package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.BeerOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface BeerOrderRepository extends JpaRepository<BeerOrderEntity, UUID> {
}
