package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
}
