package com.rose.models.account;

import com.rose.entities.Payment;
import com.rose.entities.enums.EOrderType;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryPaymentResponse implements Serializable {
        private EPaymentStatus paymentStatus;
        private String paymentDate;
        private String roseRef;
        private EPaymentMethod paymentMethod;
        private EOrderType orderType;
        private Double amount;

        public HistoryPaymentResponse(Payment payment){
            this.paymentStatus = payment.getPaymentStatus();
            this.paymentDate = payment.getPayDate() == null ? payment.getPaymentStatus().toString() : payment.getPayDate().toString();
            this.roseRef = payment.getRoseRef();
            this.paymentMethod = payment.getPaymentMethod();
            this.orderType = payment.getOrderType();
            this.amount = payment.getAmount();
        }
}
