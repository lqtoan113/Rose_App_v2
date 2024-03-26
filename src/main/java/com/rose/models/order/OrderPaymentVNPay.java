package com.rose.models.order;

import com.rose.utils.XDateUtils;
import com.rose.entities.Account;
import com.rose.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderPaymentVNPay {
    private final String vnp_Version = "2.1.0";
    private final String vnp_Command = "pay";
    private final String vnp_CurrCode = "VND";
    private final String vnp_Locale = "vn";
    private String vnp_TmnCode;
    private String vnp_OrderInfo;
    private String vnp_TxnRef;
    private String vnp_IpAddr;
    private Integer vnp_Amount;
    private String vnp_ReturnUrl;
    private String vnp_CreateDate;
    private String vnp_ExpireDate;

    public OrderPaymentVNPay(Order order, String tmnCode, String txnRef, String ipAddress, int amount, String returnUrl){
        this.vnp_TmnCode = tmnCode;
        this.vnp_OrderInfo= "Payment_"+ amount +"_for_oderID_" + order.getId()+"_of_"+order.getAccount().getUsername().replace("@", "_");
        this.vnp_TxnRef= txnRef;
        this.vnp_IpAddr = ipAddress;
        this.vnp_Amount = amount;
        this.vnp_ReturnUrl = returnUrl;
        this.vnp_CreateDate = XDateUtils.getCurrentTime();
        this.vnp_ExpireDate = XDateUtils.getTimeAfterFifteenMinutes();
    }

    public OrderPaymentVNPay(Account account, String  tmnCode, String txnRef, String ipAddress, int amount, String returnUrl){
        this.vnp_TmnCode = tmnCode;
        this.vnp_OrderInfo= "Recharge_"+ amount +"_for_"+ account.getUsername().replace("@", "_");
        this.vnp_TxnRef= txnRef;
        this.vnp_IpAddr = ipAddress;
        this.vnp_Amount = amount;
        this.vnp_ReturnUrl = returnUrl;
        this.vnp_CreateDate = XDateUtils.getCurrentTime();
        this.vnp_ExpireDate = XDateUtils.getTimeAfterFifteenMinutes();
    }
}
