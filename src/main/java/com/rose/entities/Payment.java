package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rose.config.VNPayConfig;
import com.rose.utils.XDateUtils;
import com.rose.entities.enums.EOrderType;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.order.OrderPaymentVNPay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

    @Id
    @Column(name = "rose_ref")
    private String roseRef;

    @Column(name = "vnp_TransactionNo")
    private String vnp_TransactionNo;

    @Column(name = "Ip_Address")
    private String IpAddr;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private EPaymentMethod paymentMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private EPaymentStatus paymentStatus;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    private EOrderType orderType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "vnp_BankCode")
    private String vnp_BankCode;

    @Column(name = "vnp_BankTranNo")
    private String vnp_BankTranNo;

    @Column(name = "vnp_CardType")
    private String vnp_CardType;

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
    private Date createDate;

    @Column(name = "expire_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
    private Date vnp_ExpireDate;

    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
    private Date payDate;

    @Column(name = "order_info")
    private String vnp_OrderInfo;

    @Column(name = "vnp_ResponseCode")
    private String vnp_ResponseCode;

    @Column(name = "vnp_payUrl", columnDefinition = "nvarchar(1000)")
    private String vnp_PayUrl;
    @ManyToOne
    @JsonIgnore
    private Account account;

    public Payment(OrderPaymentVNPay payment, String payUrl, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType) throws ParseException {
        this.roseRef= payment.getVnp_TxnRef();
        this.IpAddr = payment.getVnp_IpAddr();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderType = orderType;
        this.amount = VNPayConfig.convertVNDtoUSD(payment.getVnp_Amount());
        this.createDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(payment.getVnp_CreateDate());
        this.vnp_ExpireDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(payment.getVnp_ExpireDate());
        this.vnp_OrderInfo = payment.getVnp_OrderInfo();
        this.vnp_PayUrl = payUrl;
    }

    public Payment(Order order,EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType, String ipAddr) throws ParseException {
        this.roseRef = order.getId().toString();
        this.IpAddr = ipAddr;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderType = orderType;
        this.amount = order.getFinalPriceOrder();
        this.createDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(XDateUtils.getCurrentTime());
    }

    public Payment(Order order, EPaymentMethod paymentMethod,EOrderType orderType, String ipAddr) throws ParseException {
        this.roseRef = order.getId().toString();
        this.IpAddr = ipAddr;
        this.paymentMethod = paymentMethod;
        this.orderType = orderType;
        this.amount = order.getFinalPriceOrder();
        this.createDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(XDateUtils.getCurrentTime());
    }
}
