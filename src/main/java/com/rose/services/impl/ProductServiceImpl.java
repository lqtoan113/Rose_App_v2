package com.rose.services.impl;

import com.rose.entities.Product;
import com.rose.entities.ProductEntry;
import com.rose.entities.ProductSearch;
import com.rose.exceptions.CustomException;
import com.rose.models.product.ProductDto;
import com.rose.models.product.ProductRequest;
import com.rose.repositories.ProductRepository;
import com.rose.repositories.ProductSearchRepository;
import com.rose.services.IProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {
    private static final Logger LOGGER = LogManager.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductSearchRepository productSearchRepository;
    @Autowired
    private ProductEntryServiceImpl productEntryService;
    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * @return
     */
    @Override
    @Cacheable(value = "products")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    /**
     * @param pageable
     * @param cateName
     * @return
     */

    /**
     * @param productCode
     * @return
     */
    @Override
    @Cacheable(value = "product", key = "#productCode")
    public Optional<Product> getProductByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    /**
     * @param productCode
     * @return
     */
    @Override
    @Cacheable(value = "product_existByCode", key = "#productCode")
    public Boolean existProductByProductCode(String productCode) {
        return productRepository.existsProductByProductCode(productCode);
    }

    /**
     * @param productRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = CustomException.class)
    @Caching(
            evict = {@CacheEvict(value = {"products", "product_search", "product_existByCode"}, allEntries = true)}
    )
    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product(productRequest, 0, 0.0, 0.0);
        Product productSaved = productRepository.save(product);
        productSearchRepository.save(new ProductSearch(product));

        productRequest.getProductEntries().forEach(
                e -> {
                    ProductEntry productEntry = productEntryService.createProductEntry(productSaved, e);
                    productEntry.setProduct(productSaved);
                }
        );

        return productSaved;
    }

    /**
     * @return
     */
    @Override
    @Cacheable(value = "product_top_10")
    public List<Product> getTop10Sale() {
        return productRepository.getTop10ProductBestSale();
    }

    /**
     * @param product
     * @return
     */
    @Override
    @Caching(
            evict = {@CacheEvict(value = {"product", "product_search", "product_list", "products", "categories", "categoryByKeyword"}, allEntries = true)}
    )
    public Product updateProduct(Product product) {
        productSearchRepository.save(new ProductSearch(product));
        return productRepository.save(product);
    }

    /**
     * @param keyword
     * @return
     */
    @Override
    @Cacheable(value = "product_search", key = "#keyword")
    public List<ProductDto> searchProductWithElasticsearch(String keyword) {
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "code", "name", "description")
                .fuzziness(Fuzziness.AUTO).slop(1).prefixLength(3);
        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();
        try {
            SearchHits<ProductSearch> productHits = operations.search(
                    searchQuery, ProductSearch.class,
                    IndexCoordinates.of("product")
            );

            List<ProductDto> result = new ArrayList<>();
            productHits.getSearchHits().forEach(a -> result.add(new ProductDto(a.getContent())));
            return result;
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public Long getAvailable() {
        return productRepository.getAvailable();
    }

    @Override
    public Long getTotalProduct() {
        return productRepository.count();
    }

    @Override
    public List<Object[]> numberOfProductSoldByType() {
        return productRepository.numberOfProductSoldByType();
    }

    @Override
    public List<Object[]> getPercentByCate() {
        return productRepository.getPercentByCate();
    }

    @Override
    public List<Object[]> availableRate() {
        return productRepository.availableRate();
    }

    @Override
    public List<Object> top10Product() {
        return productRepository.getTop10();
    }


}
