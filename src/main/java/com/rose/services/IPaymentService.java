package com.rose.services;

import com.rose.entities.Account;
import com.rose.entities.Order;
import com.rose.entities.Payment;
import com.rose.entities.enums.EOrderType;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.order.OrderPaymentVNPay;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPaymentService {
    Payment savePayment(Payment payment);
    Payment updatePayment(Payment payment, Map map) throws ParseException;
    String generatePayUrl(OrderPaymentVNPay payment);
    Payment createPaymentRecharge(Account account, HttpServletRequest req, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType, Double amount) throws ParseException;
    Payment createPaymentCOD(Order order, HttpServletRequest request, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType) throws ParseException;
    Payment createPaymentVNPay(Order order, HttpServletRequest req, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType) throws ParseException;
    Payment createPaymentRosePay(Order order, HttpServletRequest request, EPaymentMethod paymentMethod, EOrderType orderType, Account account) throws ParseException;
    Optional<Payment> findPaymentByRoseRef(String roseRef);
    List<Payment> getHistoryPaymentByUsername(String username);
}
