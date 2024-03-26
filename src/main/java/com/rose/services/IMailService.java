package com.rose.services;


import com.rose.entities.Order;
import com.rose.models.MailObject;

public interface IMailService {
    void sendEmailForgotPassword(MailObject mail);
    void sendEmailForgotPasswordSuccess(MailObject mail);

    void sendEmailOrderSuccess(MailObject mailObject, Order order);
}
