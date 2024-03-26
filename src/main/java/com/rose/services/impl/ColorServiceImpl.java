package com.rose.services.impl;

import com.rose.entities.Color;
import com.rose.models.color.ColorRequest;
import com.rose.repositories.ColorRepository;
import com.rose.services.IColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImpl implements IColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Override
    public List<Color> getAll() {
        return colorRepository.findAll();
    }

    @Override
    public Optional<Color> getColorByColorValue(String colorValue) {
        return colorRepository.findColorsByColorValue(colorValue);
    }

    @Override
    public Optional<Color> findColorByColorValue(String colorValue) {
        return colorRepository.findColorByColorValue(colorValue);
    }

    @Override
    public boolean existsByColorValue(String colorValue) {
        return colorRepository.existsByColorValue(colorValue);
    }

    @Override
    public Color createColor(ColorRequest colorRequest) {

        Color color = new Color(
                colorRequest.getColorName(),
                colorRequest.getColorValue(),
                colorRequest.isAvailable(),
                colorRequest.getCreateDate());

        return colorRepository.save(color);
    }

    @Override
    public boolean existsByColorName(String colorName) {
        return colorRepository.existsByColorName(colorName);
    }

    @Override
    public Color updateColor(Color color) {
        return  colorRepository.save(color);
    }

    @Override
    public Optional<Color> findColorById(long id) {
        return colorRepository.findById(id);
    }
}
