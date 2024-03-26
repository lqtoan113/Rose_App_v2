package com.rose.repositories;

import com.rose.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where  c.product.productCode=?1")
    List<Comment> findAllByProductId(String productCode);

    Optional<Comment> findById(Long id);
}