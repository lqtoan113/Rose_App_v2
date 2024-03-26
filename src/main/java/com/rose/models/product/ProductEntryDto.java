package com.rose.models.product;

import com.rose.entities.ProductEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link ProductEntry} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntryDto implements Serializable {
    @NotBlank
    @Size(max = 20)
    private String sku;

    @PositiveOrZero
    private Integer quantity;

    @PositiveOrZero
    private Double productPrice;

    private Boolean available;
    @URL
    @NotNull
    private String imageUrl;

    @NotBlank
    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$", message = "Color value invalid...")
    private String colorValue;

    @NotNull
    private Set<String> sizeValue;
}