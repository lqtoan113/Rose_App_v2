package com.rose.models.comment;

import com.rose.entities.Account;
import com.rose.entities.Comment;
import com.rose.entities.Product;
import com.rose.models.account.AccountDto;
import com.rose.models.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link Comment} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {
    private  Long id;
    private  String content;
    private  boolean available;
    private  Date createDate;
    private String fullName;
    private String username;
    private String imageUrl;

    private int rate;
    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.available = comment.isAvailable();
        this.createDate = comment.getCreateDate();
        this.fullName = comment.getAccount().getFullName();
        this.imageUrl = comment.getAccount().getPhoto();
        this.username = comment.getAccount().getUsername();
        this.rate = comment.getRate();
    }
}