package com.rose.repositories;

import com.rose.entities.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findColorsByColorValue(String colorValue);

    Boolean existsByColorValue(String colorValue);

    Boolean existsByColorName(String colorName);


    Optional<Color> findColorByColorValue(String colorValue);
}

