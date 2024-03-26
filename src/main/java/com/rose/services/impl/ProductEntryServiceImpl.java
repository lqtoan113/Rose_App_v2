package com.rose.services.impl;

import com.rose.entities.Product;
import com.rose.entities.ProductEntry;
import com.rose.exceptions.CustomException;
import com.rose.models.product.ProductEntryDto;
import com.rose.repositories.ProductEntryRepository;
import com.rose.services.IProductEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductEntryServiceImpl implements IProductEntryService {
    @Autowired private ProductEntryRepository productEntryRepository;
    @Autowired private ImageServiceImpl imageService;
    @Autowired private ColorServiceImpl colorService;
    @Autowired private SizeServiceImpl sizeService;

    /**
     * @param entryDto
     * @return
     */
    @Override
    @CacheEvict(value = "product", key = "#product.productCode")
    public ProductEntry createProductEntry(Product product, ProductEntryDto entryDto) {
        if (productEntryRepository.existsBySKU(entryDto.getSku())){
            throw new CustomException(HttpStatus.NOT_FOUND, "[SKU] : "+ entryDto.getSku()+" already taken...");
        }

        ProductEntry productEntry = new ProductEntry(entryDto, 0, 0.0);
        productEntry.setProduct(product);
        productEntry.setImage(imageService.saveImage(entryDto.getImageUrl()));

        productEntry.setColor(colorService.getColorByColorValue(entryDto.getColorValue()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "[SKU] : "+ entryDto.getSku()+".Color "+ entryDto.getColorValue()+" is not exists...")
        ));

        productEntry.setSizes(sizeService.getSizeBySetString(entryDto.getSizeValue()));

        return productEntryRepository.save(productEntry);
    }

    /**
     * @param productEntry
     * @return
     */
    @Override
    @CacheEvict(value = "product", key = "#productEntry.product.productCode")
    public ProductEntry updateProductEntry(ProductEntry productEntry) {
        return productEntryRepository.save(productEntry);
    }

    /**
     * @param productEntries
     */
    @Override
    @CacheEvict(value = {"product", "products", "product_search"}, key = "#productEntries.get(0).product.productCode")
    public void updateStatusListProductEntry(List<ProductEntry> productEntries, Boolean status) {
        productEntries.forEach(
                i -> {
                    i.setAvailable(status);
                    productEntryRepository.save(i);
                });
    }

    /**
     * @param sku
     * @return
     */
    @Override
    public Optional<ProductEntry> findBySKU(String sku) {
        return productEntryRepository.findById(sku);
    }
}
