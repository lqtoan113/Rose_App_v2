package com.rose.models.color;


import com.rose.entities.Color;
import com.rose.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * A DTO for the {@link com.rose.entities.Color} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorDto implements Serializable {
    private long id;
    private String colorName;
    private String colorValue;
    private boolean available;

    public ColorDto(Color color) {
        this.id = color.getId();
        this.colorName = color.getColorName();
        this.colorValue = color.getColorValue();
        this.available = color.isAvailable();
    }

}