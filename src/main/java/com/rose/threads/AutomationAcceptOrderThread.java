package com.rose.threads;

import com.rose.entities.Order;
import com.rose.entities.enums.EOrder;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.SmsObject;
import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class AutomationAcceptOrderThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(AutomationAcceptOrderThread.class);

    private final OrderServiceImpl orderService;
    private final SmsServiceImpl smsService;

    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;

    public AutomationAcceptOrderThread(SmsServiceImpl smsService, OrderServiceImpl orderService) {
        this.orderService = orderService;
        this.smsService = smsService;
    }


    @Override
    public void run() {
        LOGGER.info("Automation accept order thread starting...");
        while (true) {
            LOGGER.info("Automation accept order thread is listening...");
            try {
                List<Order> orderList = orderService.getOrdersByStatus(EOrder.PENDING_ACCEPT);
                if (!orderList.isEmpty()) {
                    orderList = orderList.stream()
                            .filter(order -> order.getPayment().getPaymentStatus().equals(EPaymentStatus.SUCCESS))
                            .collect(Collectors.toList());
                    orderList.forEach(order -> {
                        order.setStatus(EOrder.ACCEPTED);
                        orderService.updateOrder(order);
                        smsService.sendSms(new SmsObject(defaultNumberPhone, "Order: #" + order.getId() + "with " + order.getTotalPriceOrder().toString() + "$ "
                                + "has been accepted!"));
                        LOGGER.info("Order #" + order.getId() + " has been auto accepted!");
                    });
                }
                Thread.sleep(60000 * 5);
            } catch (Exception e) {
                break;
            }
        }

    }

}
