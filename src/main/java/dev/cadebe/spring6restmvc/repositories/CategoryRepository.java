package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.data.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
