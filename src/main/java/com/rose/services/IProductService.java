package com.rose.services;

import com.rose.entities.Product;
import com.rose.models.product.ProductDto;
import com.rose.models.product.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Page<Product> getAll(Pageable pageable);

    List<Product> getAll();

    Optional<Product> getProductByProductCode(String productCode);

    Boolean existProductByProductCode(String productCode);

    Product createProduct(ProductRequest productRequest);

    List<Product> getTop10Sale();
    Product updateProduct(Product product);
    List<ProductDto> searchProductWithElasticsearch(String keywords);

    Long getAvailable();

    Long getTotalProduct();

    List<Object[]> numberOfProductSoldByType();

    List<Object[]> getPercentByCate();

    List<Object[]> availableRate();

    List<Object> top10Product();
}
