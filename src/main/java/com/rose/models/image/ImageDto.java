package com.rose.models.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * A DTO for the {@link com.rose.entities.Image} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto implements Serializable {
    private Long id;
    @URL
    private String imageUrl;
}