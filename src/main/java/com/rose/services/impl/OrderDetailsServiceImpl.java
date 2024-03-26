package com.rose.services.impl;

import com.rose.entities.*;
import com.rose.exceptions.CustomException;
import com.rose.models.order.ProductEntryOrder;
import com.rose.repositories.OrderDetailRepository;
import com.rose.services.IOrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderDetailsServiceImpl implements IOrderDetailsService {
    @Autowired private OrderDetailRepository repository;
    @Autowired private ProductEntryServiceImpl productEntryService;
    @Autowired private ProductServiceImpl productService;
    @Autowired private SizeServiceImpl sizeService;
    @Autowired private DiscountServiceImpl discountService;

    /**
     * @param order
     * @return
     */
    @Override
    @Transactional(rollbackFor = CustomException.class)
    public OrderDetail createOrderDetail(ProductEntryOrder productOrder, Order order) {
        OrderDetail orderDetail = new OrderDetail(order);
        // product
        ProductEntry productEntry = productEntryService.findBySKU(productOrder.getSku()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Product : " + productOrder.getSku() + " is not found...")
        );
        Product product = productEntry.getProduct();

        if (canBuyProductEntry(productEntry, productOrder.getQuantity(), productOrder.getSizeValue())) {
            int quantityRemaining = productEntry.getQuantity() - productOrder.getQuantity();
            double price = productOrder.getQuantity() * productEntry.getProductPrice();
            double priceDiscount = 0;
            Discount discount = product.getDiscount();
            if (discount != null){
                if (discountService.canUseDiscount(product.getDiscount())){
                    priceDiscount = productOrder.getQuantity() * (productEntry.getProductPrice() / 100 * product.getDiscount().getDiscountPercent());
                    discount.setTotalDiscountUsed(discount.getTotalDiscountUsed() + productOrder.getQuantity());
                    discount.setTotalExpense(discount.getTotalExpense() +priceDiscount);
                    discountService.update(discount);
                }
            }
            // minus quantity and update
            updateProductEntry(productEntry, quantityRemaining, productOrder, price - priceDiscount);
            updateProduct(product, productOrder, price - priceDiscount);
            return updateOrderDetail(orderDetail, productEntry, productOrder, price, priceDiscount);
        }

        return null;
    }

    private void updateProductEntry(ProductEntry productEntry, Integer quantityRemaining, ProductEntryOrder productOrder, Double price) {
        productEntry.setQuantity(quantityRemaining);
        productEntry.setSold(productEntry.getSold() + productOrder.getQuantity());
        productEntry.setRevenue(productEntry.getRevenue() + price);
        productEntryService.updateProductEntry(productEntry);
    }

    private void updateProduct(Product product, ProductEntryOrder productOrder, Double price) {
        product.setSold(product.getSold() + productOrder.getQuantity());
        product.setRevenue(product.getRevenue() + price);
        productService.updateProduct(product);
    }

    private OrderDetail updateOrderDetail(OrderDetail orderDetail, ProductEntry productEntry, ProductEntryOrder productOrder, Double price, Double priceDiscount) {
        orderDetail.setProduct(productEntry);
        orderDetail.setQuantity(productOrder.getQuantity());
        orderDetail.setTotalPrice(price);
        orderDetail.setFinalPrice(price - priceDiscount);
        orderDetail.setDiscountPercent(priceDiscount > 0 ? productEntry.getProduct().getDiscount().getDiscountPercent() : 0);
        orderDetail.setSize(sizeService.findSizeByValue(productOrder.getSizeValue()).get());
        return repository.save(orderDetail);
    }

    @Override
    public Double getTodayIncome() {
        return repository.getTodayIncome();
    }

    @Override
    public Double getTotalIncome() {
        return repository.getTotalIncome();
    }


    @Transactional(rollbackFor = CustomException.class)
    public boolean canBuyProductEntry(ProductEntry productEntry, Integer quantity, String sizeValue) {
        // available check
        if (!productEntry.getAvailable()) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Product : " + productEntry.getProduct().getProductName() + " stopped selling.");
        }
        // quantity check
        if (quantity > productEntry.getQuantity()) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, productEntry.getProduct().getProductName()
                    + " with Color:" + productEntry.getColor().getColorName() + " - Size:" + sizeValue + " is out of stock!");
        }
        // size check
        boolean isHaveNotSizeValue = productEntry.getSizes().stream().noneMatch(size -> size.getSizeValue().equalsIgnoreCase(sizeValue));
        if (isHaveNotSizeValue) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, productEntry.getProduct().getProductName()
                    + " with Color:" + productEntry.getColor().getColorName() + " doesn't have Size:" + sizeValue);
        }
        return true;
    }

}
