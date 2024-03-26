package com.rose.services;

import com.rose.entities.Color;

import com.rose.models.color.ColorRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface IColorService {
    List<Color> getAll();

    Optional<Color> getColorByColorValue(String colorValue);

    Optional<Color> findColorByColorValue(String colorValue);
    boolean existsByColorValue(String colorValue);

    Color createColor(ColorRequest colorRequest);

    boolean existsByColorName(String colorName);

    Color updateColor(Color color);

    Optional<Color> findColorById(long id);
}
