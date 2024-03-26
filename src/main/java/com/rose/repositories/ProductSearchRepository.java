package com.rose.repositories;

import com.rose.entities.ProductSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearch, String> {
}
