package com.rose.services.impl;

import com.rose.models.SmsObject;
import com.rose.services.ISmsService;
import com.rose.threads.HandleSendSMSThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class SmsServiceImpl implements ISmsService {
    @Value("${my.app.twilio.accountsid}")
    private String accountSID;
    @Value("${my.app.twilio.authtoken}")
    private String authToken;
    @Value("${my.app.twilio.phonenumber}")
    private String phoneNumber;
    @Qualifier("myThreadPool")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public void sendSms(SmsObject smsObject) {
        executor.execute(new HandleSendSMSThread(smsObject, accountSID, authToken, phoneNumber));
    }
}
