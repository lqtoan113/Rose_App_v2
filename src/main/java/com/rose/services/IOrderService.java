package com.rose.services;

import com.rose.entities.Order;
import com.rose.entities.Payment;
import com.rose.entities.enums.EOrder;
import com.rose.models.order.OrderRequest;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface IOrderService {
    List<Order> getMyOrdersByUsername(String username);

    Optional<Order> getOrderByUsernameAndId(String username, Long id);

    List<Order> getAllOrders();

    Order doCompleteOrder(Order order);
    List<Order> getOrdersByStatus(EOrder order);

    Order updateOrder(Order order);

    Order rollbackOrder(Order order);

    Optional<Order> getOrderById(Long id);
    Payment createOrderRecharge(Authentication auth, HttpServletRequest request, Double amount) throws ParseException;
    Order createOrderWithCOD(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException;

    Payment createOrderWithVNPay(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException;

    Boolean existByOrderId(Long id);

    void cancelOrderVNPayAfter15m(Payment payment, Order order);

    Order createSimpleOrder(Authentication auth, OrderRequest orderRequest);

    Order createOrderWithRosePay(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException;


    Long totalOrder();

    List<Object[]> getRevenueLast7Days();

    Long TodayAcceptOrder();

    Long TodayPendingAcceptOrder();

    Long TodayShippingOrder();

    Long TodayCompleteOrder();

    Long TodayCancelOrder();

    Long getToDayOrder();
}
