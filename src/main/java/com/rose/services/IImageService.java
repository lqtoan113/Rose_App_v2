package com.rose.services;

import com.rose.entities.Image;

import java.util.Optional;

public interface IImageService {

    Optional<Image> findById(Long id);

    Image saveImage(String imageUrl);
}
