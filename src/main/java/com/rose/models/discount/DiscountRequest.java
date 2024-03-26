package com.rose.models.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest implements Serializable {
    @NotBlank
    private String discountCode;

    @NotBlank
    private String description;

    private boolean active;

    @Min(value=0)
    @Max(value=100)
    private Integer discountPercent;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;


}
