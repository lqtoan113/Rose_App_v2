package com.rose.services.impl;

import com.google.api.Authentication;
import com.rose.entities.Account;
import com.rose.entities.Comment;
import com.rose.entities.Product;
import com.rose.models.comment.CommentRequest;
import com.rose.repositories.CommentRepository;
import com.rose.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired private CommentRepository commentRepository;
    @Autowired private ProductServiceImpl productService;
    @Override
    public List<Comment> findAllById(String id) {
        return commentRepository.findAllByProductId(id);
    }

    @Override
    @CacheEvict(value = "product", key = "#product.productCode")
    public Comment createComment(Product product, Account account, CommentRequest commentRequest) {
        Comment comment = new Comment(commentRequest.getContent(),commentRequest.getRate(), account,true, product);
        recalculationRating(product, commentRequest.getRate());
        return commentRepository.save(comment);
    }

    private void recalculationRating(Product product, Integer newRate){
        List<Integer> listRating = product.getCommentList().stream().filter(Comment::isAvailable).map(Comment::getRate).collect(Collectors.toList());
        listRating.add(newRate);
        Double avgRating = listRating.stream().mapToInt( value -> value).average().getAsDouble();
        product.setAvgRate(Double.valueOf(new DecimalFormat("#.#").format(avgRating)));
        productService.updateProduct(product);
    }
    @Override
    @CacheEvict(value = "product", key = "#comment.product.productCode")
    public Comment updateComment(Comment comment) {
        recalculationRating(comment.getProduct(), comment.getRate());
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }
}
