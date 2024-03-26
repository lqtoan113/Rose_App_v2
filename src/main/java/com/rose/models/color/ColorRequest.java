package com.rose.models.color;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorRequest {

    private long id;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 20)
    private String colorName;


    @NotBlank
    @NotNull
    @Size(min = 6, max = 10)
    private String colorValue;

    private boolean available;

    @Temporal(TemporalType.DATE)
    private Date createDate = new Date();
}
