package com.rose.models.comment;

import com.rose.entities.Account;
import com.rose.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    private Long id;
    @NotBlank
    @NotNull
    @Size(min=3, max=255)
    private  String content;
    
    @Min(value = 0)
    @Max(value = 5)
    Integer rate;
}
