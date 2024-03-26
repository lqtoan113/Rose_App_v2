package com.rose.models.size;

import com.rose.entities.Color;
import com.rose.entities.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.rose.entities.Size} entity
 */
@Data
public class SizeDto implements Serializable {
    private Long id;
    private String sizeValue;
    private boolean available;
    
    public SizeDto(Size size) {
      this.id = size.getId();
      this.sizeValue = size.getSizeValue();
      this.available = size.isAvailable();
    }
}