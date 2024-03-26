package com.rose.services.impl;

import com.rose.entities.Image;
import com.rose.repositories.ImageRepository;
import com.rose.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements IImageService {
    @Autowired private ImageRepository imageRepository;

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Image> findById(Long id) {
        return imageRepository.findById(id);
    }

    /**
     * @param imageUrl
     * @return
     */
    @Override
    public Image saveImage(String imageUrl) {
        return imageRepository.save(new Image(null, imageUrl, null));
    }
}
