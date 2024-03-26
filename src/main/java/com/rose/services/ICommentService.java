package com.rose.services;

import com.rose.entities.Account;
import com.rose.entities.Comment;
import com.rose.entities.Product;
import com.rose.models.comment.CommentRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ICommentService {

    List<Comment> findAllById(String productCode);

    Comment createComment(Product product, Account account, CommentRequest commentRequest);

    Comment updateComment(Comment comment);

    Optional<Comment> findById(Long id);

}
