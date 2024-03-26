package com.rose.services;

import com.rose.entities.Discount;
import com.rose.models.discount.DiscountRequest;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface IDiscountService {
    List<Discount> findAll();

    Discount updateDiscount(Discount discount, DiscountRequest discountRequest) throws ParseException;

    Discount doDisCountProducts(Discount discount, List<String> productsCodeList);
    void doUnDisCountProducts(Discount discount, String productsCode);

    Discount createDiscount(DiscountRequest discountRequest) throws ParseException;

    Optional<Discount> findById(String discountCode);

    boolean existByDiscountCode(String discountCode);

    Boolean canUseDiscount(Discount discount);

    Discount update(Discount discount);

}
