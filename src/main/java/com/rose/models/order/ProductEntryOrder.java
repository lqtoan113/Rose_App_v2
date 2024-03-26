package com.rose.models.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntryOrder {
    @NotBlank
    private String sku;
    @Positive
    @Min(1)
    @Max(99)
    private Integer quantity;
    @NotBlank
    private String sizeValue;
}
