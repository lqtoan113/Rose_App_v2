package com.rose.controllers.views;

import com.rose.config.VNPayConfig;
import com.rose.entities.Account;
import com.rose.entities.Order;
import com.rose.entities.Payment;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.SmsObject;
import com.rose.services.impl.AccountServiceImpl;
import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.PaymentServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OrderViewController {
    @Autowired
    SmsServiceImpl smsService;
    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;
    @Autowired
    private VNPayConfig vnPayConfig;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private PaymentServiceImpl paymentService;

    @GetMapping("/order-list")
    public String list() {
        return "user/pages/order-infor";
    }

    @GetMapping("/order-success/{orderId}")
    public String orderSuccess(@PathVariable String orderId) {
        return "user/pages/order-success";
    }

    @GetMapping("/order-payment/{orderId}")
    public String paymentPage(HttpServletRequest req, @PathVariable String orderId) throws ParseException {
        //Begin process return from VNPAY
        Map fields = new HashMap();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = (String) params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        if (vnPayConfig.hashAllFields(fields).equals(vnp_SecureHash)) {

            Order order = orderService.getOrderById((Long.parseLong(req.getParameter("vnp_TxnRef")))).get();
            Payment paymentOfOrder = order.getPayment();
            EPaymentStatus paymentStatus = paymentOfOrder.getPaymentStatus();
            if (paymentStatus.equals(EPaymentStatus.PENDING) || paymentStatus.equals(EPaymentStatus.FAILURE)) {
                if (Double.compare(order.getFinalPriceOrder(), VNPayConfig.convertVNDtoUSD(Integer.parseInt(req.getParameter("vnp_Amount")))) == 0) {
                    if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                        paymentOfOrder.setPaymentStatus(EPaymentStatus.SUCCESS);
                        smsService.sendSms(new SmsObject(defaultNumberPhone,
                                "You have successfully paid " + order.getTotalPriceOrder() + "$ for order #" + order.getId()));
                        paymentService.updatePayment(paymentOfOrder, fields);
                    } else if ("07".equals(req.getParameter("vnp_ResponseCode"))) {
                        paymentOfOrder.setPaymentStatus(EPaymentStatus.SUCCESS);
                        paymentService.updatePayment(paymentOfOrder, fields);
                        smsService.sendSms(new SmsObject(defaultNumberPhone,
                                "You have successfully paid " + order.getTotalPriceOrder() + "$ for order #" + order.getId()));
                        order.setDescription("Subtract money successfully at. Suspicious transaction (related to fraud, unusual transaction).");
                        orderService.updateOrder(order);
                    } else {
                        paymentOfOrder.setPaymentStatus(EPaymentStatus.FAILURE);
                        paymentService.updatePayment(paymentOfOrder, fields);
                        order.setDescription("The transaction failed due to a client-side error.");
                        orderService.updateOrder(order);
                    }
                }
            }
        }
        return "user/pages/order-success";
    }

    @GetMapping("/recharge/{roseRef}")
    public String paymentRechargePage(HttpServletRequest req, @PathVariable String roseRef) throws ParseException {
        //Begin process return from VNPAY
        Map fields = new HashMap();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = (String) params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        Payment payment = paymentService.findPaymentByRoseRef(req.getParameter("vnp_TxnRef")).get();
        if (vnPayConfig.hashAllFields(fields).equals(vnp_SecureHash)) {
            Account account = payment.getAccount();
            EPaymentStatus paymentStatus = payment.getPaymentStatus();
            if (paymentStatus.equals(EPaymentStatus.PENDING)) {
                if (Double.compare(payment.getAmount(), VNPayConfig.convertVNDtoUSD(Integer.parseInt(req.getParameter("vnp_Amount")))) == 0) {
                    String responseCode = req.getParameter("vnp_ResponseCode");
                    if ("00".equals(responseCode) || "07".equals(responseCode)) {
                        payment.setPaymentStatus(EPaymentStatus.SUCCESS);
                        paymentService.updatePayment(payment, fields);
                        Double balance = account.getBalance() + payment.getAmount();
                        smsService.sendSms(new SmsObject(defaultNumberPhone,
                                "You have successfully recharge " + payment.getAmount() + ".Your balance: " + balance + "$"));
                        account.setBalance(balance);
                        accountService.updateAccount(account);
                    } else {
                        payment.setPaymentStatus(EPaymentStatus.FAILURE);
                        paymentService.updatePayment(payment, fields);
                    }
                }
            }
        }
        payment.setPaymentStatus(EPaymentStatus.FAILURE);
        paymentService.updatePayment(payment, fields);
        return "user/pages/payment-status";
    }
}
