package com.rose.services.impl;

import com.rose.entities.*;
import com.rose.entities.enums.EOrder;
import com.rose.entities.enums.EOrderType;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.exceptions.CustomException;
import com.rose.models.MailObject;
import com.rose.models.SmsObject;
import com.rose.models.order.OrderRequest;
import com.rose.repositories.OrderRepository;
import com.rose.services.IOrderService;
import com.rose.threads.AutomationCancelOrderThread;
import com.rose.threads.AutomationCancelRequestRechargeThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductEntryServiceImpl productEntryService;
    @Autowired private OrderDetailsServiceImpl orderDetailsService;
    @Autowired private AccountServiceImpl accountService;
    @Autowired private MailServiceImpl mailService;
    @Autowired private PaymentServiceImpl paymentService;
    @Autowired private SmsServiceImpl smsService;
    @Autowired private ProductServiceImpl productService;
    @Qualifier("myThreadPool")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;

    @Override
    public List<Order> getMyOrdersByUsername(String username) {
        return orderRepository.getOrdersByUsername(username);
    }

    @Override
    public Optional<Order> getOrderByUsernameAndId(String username, Long id) {
        return orderRepository.getOrderByUsernameAndId(username, id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
    }

    @Override
    public Order doCompleteOrder(Order order) {
        if (order.getPayment().getPaymentMethod().equals(EPaymentMethod.COD)) {
            order.getPayment().setPaymentStatus(EPaymentStatus.SUCCESS);
        }
        order.setStatus(EOrder.COMPLETED);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(EOrder order) {
        return orderRepository.getOrdersByStatus(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order rollbackOrder(Order order) {
        Payment payment = order.getPayment();
        switch (payment.getPaymentMethod()) {
            case ROSE_PAY, VN_PAY -> {
                if (payment.getPaymentStatus().equals(EPaymentStatus.SUCCESS)) {
                    payment.setPaymentStatus(EPaymentStatus.REFUNDED);
                    Double balance = order.getAccount().getBalance() + order.getFinalPriceOrder();
                    order.getAccount().setBalance(balance);
                }
            }
        }
        List<OrderDetail> orderDetails = order.getOrderDetailList();
        orderDetails.forEach(orderDetail -> {
            Integer quantity = orderDetail.getQuantity();
            ProductEntry entry = orderDetail.getProduct();
            Product product = entry.getProduct();
            updateProductEntry(entry, orderDetail, quantity);
            updateProduct(product, orderDetail,quantity);
        });
        return orderRepository.save(order);
    }

    private void updateProductEntry(ProductEntry entry, OrderDetail orderDetail, Integer quantity){
        entry.setSold( entry.getSold() - quantity);
        entry.setRevenue( entry.getRevenue() - orderDetail.getTotalPrice());
        entry.setQuantity(entry.getQuantity() + quantity);
        productEntryService.updateProductEntry(entry);
    }

    private void updateProduct(Product product, OrderDetail orderDetail,Integer quantity){
        product.setSold( product.getSold() - quantity);
        product.setRevenue(product.getRevenue() - orderDetail.getTotalPrice());
        productService.updateProduct(product);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public Payment createOrderRecharge(Authentication auth, HttpServletRequest request, Double amount) throws ParseException {
        Account account = accountService.findByUsernameOrEmail(auth.getName()).get();
        Payment payment = paymentService.createPaymentRecharge(account, request, EPaymentMethod.VN_PAY, EPaymentStatus.PENDING, EOrderType.RECHARGE, amount);
        payment.setAccount(account);
        cancelOrderVNPayAfter15m(payment);
        return paymentService.savePayment(payment);
    }

    private void cancelOrderVNPayAfter15m(Payment payment) {
        executor.execute(new AutomationCancelRequestRechargeThread(paymentService, smsService, payment));
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public Order createOrderWithCOD(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException {
        Order order = createSimpleOrder(auth, orderRequest);
        Payment payment = paymentService.createPaymentCOD(order, request, EPaymentMethod.COD, EPaymentStatus.PENDING, EOrderType.FASHION);
        order.setPayment(payment);
        paymentService.savePayment(payment);
        smsService.sendSms(new SmsObject(defaultNumberPhone,
                "You have successfully order: #" + order.getId() + " with "+ order.getFinalPriceOrder().toString() +"$"));
        return orderRepository.save(order);
    }

    @Override
    public Boolean existByOrderId(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public Payment createOrderWithVNPay(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException {
        Order order = createSimpleOrder(auth, orderRequest);
        Payment payment = paymentService.createPaymentVNPay(order, request, EPaymentMethod.VN_PAY, EPaymentStatus.PENDING, EOrderType.FASHION);
        order.setPayment(payment);
        orderRepository.save(order);
        cancelOrderVNPayAfter15m(payment, order);
        smsService.sendSms(new SmsObject(defaultNumberPhone,
                "You have successfully order: #" + order.getId() + " with "+ order.getFinalPriceOrder().toString() +"$. "+
                "Please pay within 15 minutes! "));
        return paymentService.savePayment(payment);
    }

    @Override
    @Transactional
    public Order createOrderWithRosePay(Authentication auth, OrderRequest orderRequest, HttpServletRequest request) throws ParseException {
        // coming soon
        Account account = accountService.findByUsernameOrEmail(auth.getName()).get();
        Order order = createSimpleOrder(auth, orderRequest);
        Payment payment = paymentService.createPaymentRosePay(order, request, EPaymentMethod.ROSE_PAY, EOrderType.FASHION, account);
        order.setPayment(payment);
        paymentService.savePayment(payment);
        order.setPayment(payment);
        smsService.sendSms(new SmsObject(defaultNumberPhone,
                "You have successfully order: #" + order.getId() + " with "+ order.getFinalPriceOrder().toString() +"$"));
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrderVNPayAfter15m(Payment payment, Order order) {
        executor.execute(new AutomationCancelOrderThread(smsService,orderRepository, paymentService, this, payment, order));
    }

    @Override
    public Order createSimpleOrder(Authentication auth, OrderRequest orderRequest) {
        Account account = accountService.findByUsernameOrEmail(auth.getName()).get();
        Order order = new Order(account);
        if (StringUtils.isBlank(orderRequest.getAddress())) {
            order.setAddress(account.getAddress());
        } else {
            order.setAddress(orderRequest.getAddress());
        }

        if (StringUtils.isBlank(orderRequest.getNumberPhone())) {
            order.setAddress(account.getPhone());
        } else {
            order.setPhone(orderRequest.getNumberPhone());
        }
        Order orderSaved = orderRepository.save(order);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        //create order details
        orderRequest.getOrderDetails().forEach(o -> orderDetailList.add(orderDetailsService.createOrderDetail(o, orderSaved)));

        orderSaved.setOrderDetailList(orderDetailList);
        orderSaved.setTotalPriceOrder(orderDetailList.stream().mapToDouble(OrderDetail::getTotalPrice).sum());
        orderSaved.setFinalPriceOrder(orderDetailList.stream().mapToDouble(OrderDetail::getFinalPrice).sum());
        MailObject mailObject = new MailObject(account.getEmail(), account.getFullName(), null, null,
                "[Rose] E-Invoice",
                null, null);
        mailService.sendEmailOrderSuccess(mailObject, orderSaved);
        return orderRepository.save(orderSaved);
    }

    @Override
    public Long getToDayOrder() {
        return orderRepository.getTodayOrder();
    }

    @Override
    public Long totalOrder() {
        return orderRepository.count();
    }

    @Override
    public List<Object[]> getRevenueLast7Days() {
        return orderRepository.getRevenueLast7Days();
    }
    @Override
    public Long TodayAcceptOrder() {
        return orderRepository.TodayAcceptedOrder();
    }
    @Override
    public Long TodayPendingAcceptOrder() {
        return orderRepository.TodayPendingAcceptOrder();
    }
    @Override
    public Long TodayShippingOrder() {
        return orderRepository.TodayShippingOrder();
    }
    @Override
    public Long TodayCompleteOrder() {
        return orderRepository.TodayCompleteOrder();
    }
    @Override
    public Long TodayCancelOrder() {
        return orderRepository.TodayCancelOrder();
    }

}
