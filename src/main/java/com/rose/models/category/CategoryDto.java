package com.rose.models.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * A DTO for the {@link com.rose.entities.Category} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements Serializable {
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String categoryName;
    private boolean active;
}