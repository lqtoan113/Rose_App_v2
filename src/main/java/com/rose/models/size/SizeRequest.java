package com.rose.models.size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SizeRequest {
    private long id;

    @NotBlank
    @NotNull
    @Size(min = 1, max =20)
    private String sizeValue;

    private boolean available;

}
