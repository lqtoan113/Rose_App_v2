package com.rose.services.impl;

import com.rose.exceptions.CustomException;
import com.rose.entities.Size;
import com.rose.models.size.SizeRequest;
import com.rose.repositories.SizeRepository;
import com.rose.services.ISizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set; 
import java.util.List;
import java.util.Optional;

@Service
public class SizeServiceImpl implements ISizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    @Override
    public Optional<Size> findSizeByValue(String sizeValue) {
     return sizeRepository.findSizeBySizeValue(sizeValue);
    }

    @Override
    public boolean existsSizeByValue(String sizeValue) {
        return sizeRepository.existsBySizeValue(sizeValue);
    }

    @Override
    public Size createSize(SizeRequest sizeRequest) {
        Size size = new Size(
                sizeRequest.getId(),
                sizeRequest.getSizeValue(),
                sizeRequest.isAvailable()
        );

        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(Size size) {
        return sizeRepository.save(size);
    }
    
    @Override
    public Set<Size> getSizeBySetString(Set<String> stringSet) {
        Set<Size> sizes = new HashSet<>();
        stringSet.forEach(s -> {
            Size size = sizeRepository.findBySizeValue(s).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find size :" + s)
            );
            sizes.add(size);
        });
        return sizes;
    }

    @Override
    public Optional<Size> findSizeById(long id) {
        return sizeRepository.findById(id);
    }
}
