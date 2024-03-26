package com.rose.services.impl;

import com.rose.entities.Category;
import com.rose.entities.Product;
import com.rose.exceptions.CustomException;
import com.rose.repositories.CategoryRepository;
import com.rose.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductServiceImpl productService;
    /**
     * @param categoryCode
     * @return
     */
    @Override
    @Cacheable(value = "categoryByCode", key = "#categoryCode")
    public Optional<Category> findByCategoryCode(String categoryCode) {
        return categoryRepository.findById(categoryCode);
    }

    @Override
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    /**
     * @param categoryCode
     * @return
     */
    @Override
//    @Cacheable(value = "cate_existByCode", key = "#categoryCode")
    public Boolean existByCategoryCode(String categoryCode) {
        return categoryRepository.existsById(categoryCode);
    }

    /**
     * @param categoryName
     * @return
     */
    @Override
//    @Cacheable(value = "cate_existByName", key = "#categoryName")
    public Boolean existByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    /**
     * @param keyword
     * @return
     */
    @Override
    @Cacheable(value = "categoryByKeyword", key = "#keyword")
    public Optional<Category> findCategoryByKeyword(String keyword) {
        return categoryRepository.findCategoryByKeyword(keyword);
    }

    /**
     * @param strings
     * @return
     */
    @Override
    public Set<Product> getSetProductFromSetString(Set<String> strings) {
        Set<Product> products = new HashSet<>();

        strings.forEach( s -> {
            Product product = productService.getProductByProductCode(s).orElseThrow(
                    () -> new CustomException(HttpStatus.BAD_REQUEST, s +" is not found...")
            );
            products.add(product);
        });
        return products;
    }

    /**
     * @param category
     * @return
     */
    @Override
    @CacheEvict(value = "categories",allEntries = true)
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * @param category
     */
    @Override
    @Caching(
            evict = @CacheEvict(value = {"categoryByKeyword", "categoryByCode"},key = "#category.categoryCode")
    )
    @CacheEvict(value = "categories",allEntries = true)
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    /**
     * @param category
     * @return
     */
    @Override
    @Caching(
            evict = @CacheEvict("categories")
    )
    @CacheEvict(value = {"categoryByCode", "categoryByKeyword"},key = "#category.categoryCode")
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * @param category
     * @param listProductCode
     */
    @Override
    @Transactional(rollbackFor = CustomException.class)
    @Caching(
            evict = @CacheEvict(value = {"categoryByKeyword", "categoryByCode"},key = "#category.categoryCode")
    )
    @CacheEvict(value = "categories",allEntries = true)
    public void deleteProductsInCategory(Category category, List<String> listProductCode) {
        Set<Product> products = category.getProducts();
        listProductCode.forEach(i -> {
            products.removeIf(product -> product.getProductCode().equals(i));
        });
        category.setProducts(products);
        categoryRepository.save(category);
    }

    /**
     * @param category
     * @param listProductCode
     */
    @Override
    @Caching(
            evict = @CacheEvict(value = {"categoryByKeyword", "categoryByCode"},key = "#category.categoryCode")
    )
    @CacheEvict(value = "categories",allEntries = true)
    public void addProductsToCategory(Category category, List<String> listProductCode) {
        Set<Product> products = category.getProducts();
        listProductCode.forEach(i -> {
            Product product = productService.getProductByProductCode(i).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, i+ " is not found...")
            );
            products.add(product);
        });
        category.setProducts(products);
        categoryRepository.save(category);
    }

    /**
     * @return
     */
    @Override
    @Cacheable(value = "categories")
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
