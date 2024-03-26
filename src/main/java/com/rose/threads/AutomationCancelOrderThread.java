package com.rose.threads;

import com.rose.entities.Order;
import com.rose.entities.Payment;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.SmsObject;
import com.rose.repositories.OrderRepository;
import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.PaymentServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class AutomationCancelOrderThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(AutomationCancelOrderThread.class);
    private final OrderRepository orderRepository;
    private final PaymentServiceImpl paymentService;
    private final OrderServiceImpl orderService;
    private final SmsServiceImpl smsService;
    private final Payment payment;
    private final Order order;
    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;

    public AutomationCancelOrderThread(SmsServiceImpl smsService, OrderRepository orderRepository, PaymentServiceImpl paymentService, OrderServiceImpl orderService, Payment payment, Order order) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.payment = payment;
        this.order = order;
        this.smsService = smsService;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(60000 * 15);
            if (payment.getPaymentStatus().equals(EPaymentStatus.PENDING)) {
                payment.setPaymentStatus(EPaymentStatus.TIMEOUT);
                order.setDescription("The order is automatically cancelled because the buyer does not pay within the time allowed!");
                orderService.rollbackOrder(order);
                orderRepository.save(order);
                paymentService.savePayment(payment);
                smsService.sendSms(new SmsObject(defaultNumberPhone, "Order: #" + order.getId() + "with " + order.getTotalPriceOrder().toString() + "$ "
                        + "was canceled for non-payment within the time allowed!"));
                LOGGER.info("Order #" + order.getId() + " automatically canceled cause timeout.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
