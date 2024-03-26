package com.rose.threads;

import com.rose.entities.Payment;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.SmsObject;
import com.rose.services.impl.PaymentServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class AutomationCancelRequestRechargeThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(AutomationCancelRequestRechargeThread.class);
    private final PaymentServiceImpl paymentService;
    private final SmsServiceImpl smsService;
    private final Payment payment;

    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;

    public AutomationCancelRequestRechargeThread(PaymentServiceImpl paymentService, SmsServiceImpl smsService, Payment payment) {
        this.paymentService = paymentService;
        this.payment = payment;
        this.smsService = smsService;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(60000 * 15);
            if (payment.getPaymentStatus().equals(EPaymentStatus.PENDING)) {
                payment.setPaymentStatus(EPaymentStatus.TIMEOUT);
                smsService.sendSms(new SmsObject(defaultNumberPhone,
                        "Your order: #" + payment.getRoseRef() + " with " + payment.getAmount().toString() + "$ "
                                + "was canceled for non-payment within the time allowed."));
                paymentService.savePayment(payment);
                LOGGER.info("Payment recharge #" + payment.getRoseRef() + " automatically canceled cause timeout.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
