package com.rose.services.impl;

import com.rose.entities.Discount;
import com.rose.entities.Product;
import com.rose.exceptions.CustomException;
import com.rose.models.discount.DiscountRequest;
import com.rose.repositories.DiscountRepository;
import com.rose.services.IDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DiscountServiceImpl implements IDiscountService {
    @Autowired private DiscountRepository discountRepository;
    @Autowired private ProductServiceImpl productService;
    @Override
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public Discount updateDiscount(Discount discount, DiscountRequest discountRequest) throws ParseException {
        discount.setActive(discountRequest.isActive());
        discount.setDiscountPercent(discountRequest.getDiscountPercent());
        discount.setDescription(discountRequest.getDescription());
        discount.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountRequest.getStartTime()));
        discount.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountRequest.getEndTime()));
        return discountRepository.save(discount);
    }

    @Override
    @CacheEvict(value = {"products", "product_list", "product"})
    public Discount doDisCountProducts(Discount discount, List<String> productsCodeList) {
        Set<Product> products = discount.getProducts();
        productsCodeList.forEach(i -> {
            Product product = productService.getProductByProductCode(i).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, i+ " is not found...")
            );
            products.add(product);
            product.setDiscount(discount);
            productService.updateProduct(product);
        });
        discount.setProducts(products);
        return discountRepository.save(discount);
    }

    @Override
    @CacheEvict(value = {"products", "product_list", "product"})
    public void doUnDisCountProducts(Discount discount, String productsCode) {
        Set<Product> products = discount.getProducts();
        Product product = productService.getProductByProductCode(productsCode).get();
        if (products.contains(product)){
            product.setDiscount(null);
            productService.updateProduct(product);
            products.remove(product);
        }
        discount.setProducts(products);
        discountRepository.save(discount);
    }


    @Override
    public Discount createDiscount(DiscountRequest discountRequest) throws ParseException {
        Discount discount = new Discount(discountRequest.getDiscountCode(),
                discountRequest.getDescription(),
                discountRequest.getDiscountPercent(),
                discountRequest.isActive(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountRequest.getStartTime()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountRequest.getEndTime()),
                0, 0.0);
        return discountRepository.save(discount);
    }

    @Override
    public Optional<Discount> findById(String discountCode) {
        return discountRepository.findById(discountCode);
    }

    @Override
    public boolean existByDiscountCode(String discountCode) {
        return discountRepository.existsByDiscountCode(discountCode);
    }

    @Override
    public Boolean canUseDiscount(Discount discount) {
        if (discount.getActive()){
            long now = new Date().getTime();
            return discount.getEndTime().getTime() - now > 0 && discount.getStartTime().getTime() - now <0;
        }
        return false;
    }

    @Override
    @CacheEvict(value = {"products", "product_list", "product"})
    public Discount update(Discount discount) {
        return discountRepository.save(discount);
    }

}
