package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.BeerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<BeerEntity, UUID> {
}