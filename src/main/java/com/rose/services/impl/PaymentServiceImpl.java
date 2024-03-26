package com.rose.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rose.config.VNPayConfig;
import com.rose.utils.XDateUtils;
import com.rose.entities.Account;
import com.rose.entities.Order;
import com.rose.entities.Payment;
import com.rose.entities.enums.EOrderType;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.models.order.OrderPaymentVNPay;
import com.rose.repositories.PaymentRepository;
import com.rose.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private VNPayConfig vnPayConfig;
    @Autowired
    private AccountServiceImpl accountService;
    @Value("${my.app.vnpay.payurl}")
    private String vnp_PayUrl;
    @Value("${my.app.vnpay.returnurl}")
    private String vnp_ReturnUrl;
    @Value("${my.app.vnpay.returnrechargeurl}")
    private String vnp_RechargeReturnUrl;
    @Value("${my.app.vnpay.tmncode}")
    private String vnp_TmnCode;
    @Value("${my.app.vnpay.hashsecret}")
    private String vnp_HashSecret;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(Payment paymentOfOrder, Map fields) throws ParseException {
        paymentOfOrder.setVnp_TransactionNo(fields.get("vnp_TransactionNo").toString());
        paymentOfOrder.setVnp_BankCode(fields.get("vnp_BankCode").toString());
        if (fields.containsKey("vnp_BankTranNo")){
            paymentOfOrder.setVnp_BankTranNo(fields.get("vnp_BankTranNo").toString());
        }
        paymentOfOrder.setVnp_CardType(fields.get("vnp_CardType").toString());
        paymentOfOrder.setPayDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(fields.get("vnp_PayDate").toString()));
        paymentOfOrder.setVnp_ResponseCode(fields.get("vnp_ResponseCode").toString());
        return paymentRepository.save(paymentOfOrder);
    }

    @Override
    public Payment createPaymentVNPay(Order order, HttpServletRequest req, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType) throws ParseException {
        int amount = vnPayConfig.convertUSDtoVND(order.getFinalPriceOrder()) * 100;
        OrderPaymentVNPay orderPayment = new OrderPaymentVNPay(order, vnp_TmnCode, order.getId().toString(),
                vnPayConfig.getIpAddress(req), amount, vnp_ReturnUrl + "/" + order.getId()
        );
        String payUrl = generatePayUrl(orderPayment);
        return new Payment(orderPayment, payUrl, paymentMethod, paymentStatus, orderType);
    }
    @Override
    public Payment createPaymentRecharge(Account account, HttpServletRequest req, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType, Double amount) throws ParseException {
        int amountUSD = vnPayConfig.convertUSDtoVND(amount) * 100;
        String roseRef = vnPayConfig.getRandomNumber(8);
        OrderPaymentVNPay orderPayment = new OrderPaymentVNPay(account,vnp_TmnCode, roseRef,
                vnPayConfig.getIpAddress(req), amountUSD, vnp_RechargeReturnUrl + "/" + roseRef
        );
        String payUrl = generatePayUrl(orderPayment);
        return new Payment(orderPayment, payUrl, paymentMethod, paymentStatus, orderType);
    }
    @Override
    public String generatePayUrl(OrderPaymentVNPay payment) {
        Map props = new ObjectMapper().convertValue(payment, Map.class);
        //Build data to hash and querystring
        List fieldNames = new ArrayList(props.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = props.get(fieldName).toString();
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.HMAC_SHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnp_PayUrl + "?" + queryUrl;
    }

    @Override
    public Payment createPaymentCOD(Order order, HttpServletRequest request, EPaymentMethod paymentMethod, EPaymentStatus paymentStatus, EOrderType orderType) throws ParseException {
        return new Payment(order, paymentMethod, paymentStatus, orderType, vnPayConfig.getIpAddress(request));
    }

    @Override
    public Payment createPaymentRosePay(Order order, HttpServletRequest request, EPaymentMethod paymentMethod, EOrderType orderType, Account account) throws ParseException {
        Payment payment = new Payment(order, paymentMethod, orderType, vnPayConfig.getIpAddress(request));

        if (account.getBalance() < order.getTotalPriceOrder()) {
            payment.setPaymentStatus(EPaymentStatus.FAILURE);
        } else {
            Double balance_remaining = account.getBalance() - order.getTotalPriceOrder();
            account.setBalance(balance_remaining);
            accountService.updateAccount(account);
            payment.setPaymentStatus(EPaymentStatus.SUCCESS);
            payment.setPayDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(XDateUtils.getCurrentTime()));
        }
        return payment;
    }

    @Override
    public Optional<Payment> findPaymentByRoseRef(String roseRef) {
        return paymentRepository.findById(roseRef);
    }

    @Override
    public List<Payment> getHistoryPaymentByUsername(String username) {
        return paymentRepository.getHistoryPaymentByAccount(username);
    }
}
