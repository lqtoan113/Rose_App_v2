package com.rose.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailObject
{
    private String mailTo;
    private String mailToPeople;
    private String mailCc;
    private String mailBcc;
    private String mailSubject;
    private String mailContent;
    private List<Object> attachments;

    public Date getMailSendDate() {
        return new Date();
    }
}
