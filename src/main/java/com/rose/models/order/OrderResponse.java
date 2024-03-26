package com.rose.models.order;

import com.rose.entities.enums.EOrder;
import com.rose.entities.Order;
import com.rose.entities.OrderDetail;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link com.rose.entities.Order} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse implements Serializable {
    private Long id;
    private String fullName;
    private String phone;
    private Integer totalItem;
    private Double totalMoney;
    private Double discountMoney;
    private Double finalMoney;
    private EOrder status;
    private EPaymentStatus paymentStatus;
    private EPaymentMethod paymentMethod;
    private String description;
    private String vnp_TransactionNo;
    private Date createDate;

    public OrderResponse(Order order){
        this.id = order.getId();
        this.fullName = order.getAccount().getFullName();
        this.phone = StringUtils.isBlank(order.getAccount().getPhone()) ? "Not available" : order.getAccount().getPhone();
        this.totalItem = order.getOrderDetailList().size();
        this.totalMoney = order.getTotalPriceOrder();
        this.status = order.getStatus();
        this.paymentMethod = order.getPayment().getPaymentMethod();
        this.paymentStatus= order.getPayment().getPaymentStatus();
        this.description = order.getDescription();
        this.createDate = order.getCreateDate();
        this.vnp_TransactionNo = order.getPayment().getVnp_TransactionNo();
        this.finalMoney = order.getFinalPriceOrder();
        this.discountMoney = order.getFinalPriceOrder() - order.getTotalPriceOrder();
    }
}