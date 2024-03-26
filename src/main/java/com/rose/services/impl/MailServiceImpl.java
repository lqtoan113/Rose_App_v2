package com.rose.services.impl;

import com.rose.config.CustomTableConfig;
import com.rose.entities.Order;
import com.rose.models.MailObject;
import com.rose.services.IMailService;
import com.rose.threads.HandleSendMailThread;
import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.config.TbConfiguration;
import io.rocketbase.mail.model.HtmlTextEmail;
import io.rocketbase.mail.table.TableCellHtmlSimple;
import io.rocketbase.mail.table.TableCellImageSimple;
import io.rocketbase.mail.table.TableCellLinkSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;

@Service
public class MailServiceImpl implements IMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Qualifier("myThreadPool")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public void sendEmailForgotPassword(MailObject mail) {
        EmailTemplateBuilder.EmailTemplateConfigBuilder BUILDER = new EmailTemplateBuilder.EmailTemplateConfigBuilder();
        HtmlTextEmail htmlTextEmail = BUILDER
                .header()
                .logo("https://lh3.googleusercontent.com/ogw/AOh-ky3U3tWDJqp8sRh05Dtd8cJTuS5mfvrzRDGMJS5Y=s32-c-mo").logoHeight(41)
                .and()
                .text("Welcome," + mail.getMailToPeople() + " !").h1().center().and()
                .text("We heard that you lost your Rose password. Sorry about that!").and()
                .text("But don’t worry! You can use the following button to reset your password:").and()
                .button("Reset your password", "http://localhost:8080/reset-password?token="+mail.getMailContent()).blue().and()
                .text("If you don’t use this link within 5 minutes, it will expire!").and()
                .html("If you have any questions, feel free to <a href=\"mailto:rose.app.service@gmail.com\">email our customer success team</a>. (We're lightning quick at replying.)",
                        "If you have any questions, feel free to email our customer success team\n" +
                                "(We're lightning quick at replying.) We also offer live chat during business hours.").and()
                .text("Thanks,\n" +
                        "The Rose Team").and()
                .copyright("FPT Polytechnic").url("http://localhost:8080/").suffix(". All rights reserved.").and()
                .footerText("Rose Company, LLC]\n" +
                        "1234 QTSC.\n" +
                        "Hồ Chí Minh").and()
                .footerImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg").width(100)
                .build();
        doSendMail(mail, htmlTextEmail);
    }

    @Override
    public void sendEmailForgotPasswordSuccess(MailObject mail) {
        EmailTemplateBuilder.EmailTemplateConfigBuilder BUILDER = new EmailTemplateBuilder.EmailTemplateConfigBuilder();
        HtmlTextEmail htmlTextEmail = BUILDER
                .header()
                .logo("https://lh3.googleusercontent.com/ogw/AOh-ky3U3tWDJqp8sRh05Dtd8cJTuS5mfvrzRDGMJS5Y=s32-c-mo").logoHeight(41)
                .and()
                .text("Welcome," + mail.getMailToPeople() + " !").h1().center().and()
                .text("We wanted to let you know that your Rose password was reset").and()
                .text("If you did not perform this action, you can recover access by entering "+ mail.getMailTo() +" into the form at http://localhost:8080/rest/forgot-password").and()
                .attribute()
                .keyValue("Time at", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa z").format(mail.getMailSendDate())).and()
                .html("If you have any questions, feel free to <a href=\"mailto:rose.app.service@gmail.com\">email our customer success team</a>. (We're lightning quick at replying.)",
                        "If you have any questions, feel free to email our customer success team\n" +
                                "(We're lightning quick at replying.) We also offer live chat during business hours.").and()
                .text("Thanks,\n" +
                        "The Rose Team").and()
                .copyright("FPT Polytechnic").url("http://localhost:8080/").suffix(". All rights reserved.").and()
                .footerText("Rose Company, LLC]\n" +
                        "1234 QTSC.\n" +
                        "Hồ Chí Minh").and()
                .footerImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg").width(100)
                .build();
        doSendMail(mail, htmlTextEmail);
    }

    /**
     * @param mail
     */
    @Override
    public void sendEmailOrderSuccess(MailObject mail, Order order) {
        EmailTemplateBuilder.EmailTemplateConfigBuilder BUILDER = new EmailTemplateBuilder.EmailTemplateConfigBuilder();
        TbConfiguration config = TbConfiguration.newInstance();

        BUILDER.configuration(config)
                .header().text("Invoice "+order.getId()).and()
                .text("Hi "+mail.getMailToPeople()+",").and()
                .text("Thanks for using our services. This is an invoice for your recent purchase");

        CustomTableConfig customTable = new CustomTableConfig(BUILDER);
        order.getOrderDetailList().forEach(o -> {
            customTable.itemRow(new TableCellImageSimple(o.getProduct().getImage().getImageUrl()).width(80),
                    new TableCellLinkSimple(o.getProduct().getProduct().getProductName()+" - "
                            + o.getProduct().getColor().getColorName()+" / "+ o.getSize().getSizeValue(),
                            "http://localhost:8080/product-detail/"+o.getProduct().getProduct().getProductCode()),
                    " x" + o.getQuantity(),
                    o.getQuantity() * o.getProduct().getProductPrice());
        });
        customTable.footerRow(new TableCellHtmlSimple("<b>Total</b>", "Total"),
                new TableCellHtmlSimple("<b>"+order.getFinalPriceOrder()+"$</b>",
                        new DecimalFormat("#,000.00").format(order.getFinalPriceOrder())+"$"));

        BUILDER.table(customTable)
                .button("View History Order", "http://localhost:8080/order-list").gray().right().and()
                .text("If you have any questions about this receipt, simply reply to this email or reach out to our support team for help.").and()
                .copyright("FPT Polytechnic").url("http://localhost:8080/").suffix(". All rights reserved.").and()
                .footerText("Rose Company, LLC]\n" +
                        "1234 QTSC.\n" +
                        "Hồ Chí Minh").and()
                .footerImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg").width(100)
                .build();
        HtmlTextEmail htmlTextEmail = BUILDER.build();
        doSendMail(mail, htmlTextEmail);
    }

    private void doSendMail(MailObject mail, HtmlTextEmail htmlTextEmail) {
        executor.execute(new HandleSendMailThread(mail,htmlTextEmail, javaMailSender));
    }

}
