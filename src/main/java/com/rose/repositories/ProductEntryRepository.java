package com.rose.repositories;

import com.rose.entities.ProductEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEntryRepository extends JpaRepository<ProductEntry, String> {
    boolean existsBySKU(String SKU);
}