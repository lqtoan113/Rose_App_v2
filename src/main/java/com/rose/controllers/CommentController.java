package com.rose.controllers;

import com.rose.entities.Account;
import com.rose.entities.Comment;
import com.rose.entities.Product;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.comment.CommentDto;
import com.rose.models.comment.CommentRequest;
import com.rose.services.impl.AccountServiceImpl;
import com.rose.services.impl.CommentServiceImpl;
import com.rose.services.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class CommentController {

    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private CommentServiceImpl commentService;

    @Operation(summary = "Get all comment belong to product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/products/{productCode}/comments")
    public ResponseEntity<ResponseObject> getAllCommentByProductCode(@PathVariable String productCode) {
        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                ()-> new CustomException(HttpStatus.NOT_FOUND, "Product is not found!")
        );
        if (!product.getAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available", null, null)
            );
        }
        List<CommentDto> listComments = product.getCommentList()
                .stream().filter(Comment::isAvailable)
                .map(CommentDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", listComments, listComments.size())
        );
    }

    @Operation(summary = "Create comment to product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found or not available", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping("/products/{productCode}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> createComment(Authentication authentication, @PathVariable String productCode, @RequestBody @Valid CommentRequest request) {

        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                ()-> new CustomException(HttpStatus.NOT_FOUND, "Product is not found!")
        );
        if (!product.getAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available", null, null)
            );
        }
        Account account = accountService.findByUsernameOrEmail(authentication.getName()).get();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Comment successfully!",
                        new CommentDto(commentService.createComment(product, account, request)), 1)
        );
    }

    @Operation(summary = "Update your comment to product...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found or not available", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "406", description = "Can't update comment belong to another account!", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping ("/products/{productCode}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> updateComment(Authentication authentication, @PathVariable String productCode,@RequestBody @Valid CommentRequest request) {
        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                ()-> new CustomException(HttpStatus.NOT_FOUND, "Product is not found!")
        );
        if (!product.getAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available", null, null)
            );
        }
        Comment comment = commentService.findById(request.getId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Comment not found!")
        );

        if (!comment.isAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Your comment has been banned", null, null)
            );
        }
       if (!comment.getAccount().getUsername().equals(authentication.getName())){
           return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                   new ResponseObject("NOT_ACCEPTABLE", "You can't update comment belong to another account!",null, null)
           );
       }
       comment.setContent(request.getContent());
       comment.setRate(request.getRate());
       return ResponseEntity.status(HttpStatus.OK).body(
               new ResponseObject("OK", "Update successfully!", commentService.updateComment(comment), 1)
       );
    }

    @Operation(summary = "Admin delete comment of product...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found or not available", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "406", description = "Can't update comment belong to another product!", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("management/products/{productCode}/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteCommentForAdmin(@PathVariable String productCode, @PathVariable Long commentId) {
        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                ()-> new CustomException(HttpStatus.NOT_FOUND, "Product is not found!")
        );
        if (!product.getAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available", null, null)
            );
        }
        Comment comment = commentService.findById(commentId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Comment is not exist...")
        );
        if (product.getCommentList().stream().noneMatch(c -> c.equals(comment))){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't delete comment belong to another product!", null, null)
            );
        }
        comment.setAvailable(!comment.isAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete successfully!", commentService.updateComment(comment), 1)
        );
    }
    @Operation(summary = "User delete comment of product...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found or not available", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "406", description = "Can't update comment belong to another product!", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/products/{productCode}/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> deleteCommentForUser(@PathVariable String productCode,Authentication authentication, @PathVariable Long commentId) {
        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                ()-> new CustomException(HttpStatus.NOT_FOUND, "Product is not found!")
        );
        if (!product.getAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available", null, null)
            );
        }
        Comment comment = commentService.findById(commentId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Comment is not exist...")
        );
        if (!comment.isAvailable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Comment is not available", null, null)
            );
        }
        if (!comment.getAccount().getUsername().equals(authentication.getName())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't delete comment belong to another account!",null, null)
            );
        }
        if (product.getCommentList().stream().noneMatch(c -> c.getId().equals(comment.getId()))){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't delete comment belong to another product!", null, null)
            );
        }
        comment.setAvailable(!comment.isAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete successfully!", commentService.updateComment(comment), 1)
        );
    }
}
