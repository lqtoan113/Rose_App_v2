package com.rose.services;

import com.rose.entities.Category;
import com.rose.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICategoryService {
    Optional<Category> findByCategoryCode(String categoryCode);

    Page<Category> getAll(Pageable pageable);

    Boolean existByCategoryCode(String categoryCode);

    Boolean existByCategoryName(String categoryName);

    Optional<Category> findCategoryByKeyword(String keyword);

    Set<Product> getSetProductFromSetString(Set<String> strings);

    Category createCategory(Category category);

    void deleteCategory(Category category);
    Category updateCategory(Category category);
    void deleteProductsInCategory(Category category, List<String> listProductCode);

    void addProductsToCategory(Category category, List<String> listProductCode);

    List<Category> findAll();


}
