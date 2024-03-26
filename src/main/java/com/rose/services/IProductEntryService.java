package com.rose.services;

import com.rose.entities.Product;
import com.rose.entities.ProductEntry;
import com.rose.models.product.ProductEntryDto;

import java.util.List;
import java.util.Optional;

public interface IProductEntryService {
    ProductEntry createProductEntry(Product product, ProductEntryDto entryDto);
    ProductEntry updateProductEntry(ProductEntry productEntry);

    void updateStatusListProductEntry(List<ProductEntry> productEntries, Boolean status);

    Optional<ProductEntry> findBySKU(String sku);
}
