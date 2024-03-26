package com.rose.models.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank
    @Size(min = 2, max = 10)
    private String productCode;

    @NotBlank
    @Size(min = 3, max = 50)
    private String productName;
    private Boolean available;
    private String description;

    @URL
    private String imageUrl;

    @PositiveOrZero
    private Double commonPrice;

    private List<ProductEntryDto> productEntries;
}
