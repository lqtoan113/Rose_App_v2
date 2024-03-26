package com.rose.repositories;

import com.rose.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount,String> {

    Boolean existsByDiscountCode(String discountCode);
}
