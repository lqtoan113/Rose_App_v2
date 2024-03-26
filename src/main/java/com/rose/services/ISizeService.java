package com.rose.services;

import java.util.Set;
import com.rose.entities.Color;
import com.rose.entities.Size;
import com.rose.models.color.ColorRequest;
import com.rose.models.size.SizeRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ISizeService {
    Set<Size> getSizeBySetString(Set<String> strings);
    
    List<Size> getAll();

    Optional<Size> findSizeByValue(String sizeValue);

    boolean existsSizeByValue(String sizeValue);

    Size createSize(SizeRequest sizeRequest);

    Size updateSize(Size size);

    Optional<Size> findSizeById(long id);
}
