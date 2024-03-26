package com.rose.threads;

import com.rose.models.SmsObject;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleSendSMSThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(HandleSendSMSThread.class);
    private final String accountSID;
    private final String authToken;
    private final String phoneNumber;
    private final SmsObject smsObject;

    public HandleSendSMSThread(SmsObject smsObject, String accountSID, String authToken, String phoneNumber) {
        this.accountSID = accountSID;
        this.authToken = authToken;
        this.smsObject = smsObject;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void run() {
        try {
            Twilio.init(accountSID, authToken);
            Message message = Message.creator(
                    new PhoneNumber(smsObject.getTo()),
                    new PhoneNumber(phoneNumber),
                    smsObject.getMessage()
            ).create();
            LOGGER.info("Thread sent message: " + smsObject.getMessage() + " to " + smsObject.getTo() + " SID: " + message.getSid());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

    }

}
