package com.rose.threads;

import com.rose.exceptions.CustomException;
import com.rose.models.MailObject;
import io.rocketbase.mail.model.HtmlTextEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

public class HandleSendMailThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(HandleSendMailThread.class);
    private final JavaMailSender javaMailSender;
    private final MailObject mail;
    private final HtmlTextEmail htmlTextEmail;

    public HandleSendMailThread(MailObject mail, HtmlTextEmail htmlTextEmail, JavaMailSender javaMailSender) {
        this.mail = mail;
        this.javaMailSender = javaMailSender;
        this.htmlTextEmail = htmlTextEmail;
    }

    @Override
    public void run() {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setSubject(mail.getMailSubject());
            mimeMessageHelper.setTo(mail.getMailTo());
            mimeMessageHelper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Thread sent mail to " + mail.getMailTo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(HttpStatus.CONFLICT, "Send mail to " + mail.getMailTo() + " failed...");
        }

    }

}
