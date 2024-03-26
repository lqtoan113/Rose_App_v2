package com.rose.repositories;


import com.rose.entities.Color;
import com.rose.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findBySizeValue(String sizeValue);
    
    Optional<Size> findSizeBySizeValue(String sizeValue);

    Boolean existsBySizeValue(String sizeValue);


}