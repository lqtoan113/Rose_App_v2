package com.rose.repositories;

import com.rose.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("SELECT c FROM Category c where c.categoryCode=:keyword or c.categoryName=:keyword")
    Optional<Category> findCategoryByKeyword(String keyword);

    Boolean existsByCategoryName(String categoryName);
}