package com.rose.models.order;

import com.rose.entities.enums.EOrder;
import com.rose.entities.Order;
import com.rose.entities.OrderDetail;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUserResponse implements Serializable {
    private Long id;

    private Integer totalItem;
    private Double totalMoney;
    private Double finalMoney;
    private Double discountMoney;
    private EOrder status;
    private String description;
    private EPaymentStatus paymentStatus;
    private EPaymentMethod paymentMethod;
    private Date createDate;

    public OrderUserResponse(Order order){
        this.id = order.getId();
        this.totalItem = order.getOrderDetailList().size();
        this.totalMoney = order.getTotalPriceOrder();
        this.status = order.getStatus();
        this.createDate = order.getCreateDate();
        this.paymentMethod = order.getPayment().getPaymentMethod();
        this.paymentStatus= order.getPayment().getPaymentStatus();
        this.description = order.getDescription();
        this.finalMoney = order.getFinalPriceOrder();
        this.discountMoney = order.getFinalPriceOrder() - order.getTotalPriceOrder();
    }
}
