package com.pino.mailsample.manager;

import com.pino.mailsample.model.MailConfig;
import com.pino.mailsample.model.MailMessage;
import com.pino.mailsample.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class MailManager {

    private static final Properties DEFAULT_MAIL_PROPERTIES;

    static {
        DEFAULT_MAIL_PROPERTIES = new Properties();
        DEFAULT_MAIL_PROPERTIES.put("mail.smtp.auth", "true");
        DEFAULT_MAIL_PROPERTIES.put("mail.smtp.starttls.enable", "true");
        DEFAULT_MAIL_PROPERTIES.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        DEFAULT_MAIL_PROPERTIES.put("mail.smtp.timeout", "25000");
        DEFAULT_MAIL_PROPERTIES.put("mail.smtp.ssl.protocols", "TLSv1.2");

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    private final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    private final Properties mailProperties = DEFAULT_MAIL_PROPERTIES;

    @Value("${mail.sender-username}")
    private String username;
    @Value("${mail.sender-email}")
    private String senderMail;
    @Value("${mail.sender-password}")
    private String password;
    @Value("${mail.server-host}")
    private String mailHost;
    @Value("${mail.server-port}")
    private Integer mailPort;
    @Value("${mail.tsl-enabled}")
    private String tlsEnabled;
    @Value("${mail.encoding}")
    private String encoding;
    @Value("${mail.ssl-protocol}")
    private String sslProtocols;

    public CompletableFuture<List<MailMessage>> sendMailImmediately(List<MailMessage> mailMessages) {
        ObjectUtils.checkNull(mailMessages, "The input mail messages is null");
        return this.doSendMails(mailMessages);
    }

    private CompletableFuture<List<MailMessage>> doSendMails(List<MailMessage> mailMessageList) {

        MailConfig mailConfig = this.fetchMailConfig();
        this.mailProperties.setProperty("mail.smtp.starttls.enable", mailConfig.getTlsEnabled());
        this.mailProperties.setProperty("mail.smtp.ssl.protocols", mailConfig.getSslProtocols());
        this.javaMailSender.setJavaMailProperties(this.mailProperties);
        this.javaMailSender.setUsername(mailConfig.getUsername());
        this.javaMailSender.setPassword(mailConfig.getPassword());
        this.javaMailSender.setHost(mailConfig.getMailHost());
        this.javaMailSender.setPort(mailConfig.getMailPort());
        this.javaMailSender.setDefaultEncoding(mailConfig.getEncoding());

        return CompletableFuture.supplyAsync(() -> {
            Thread.currentThread().setContextClassLoader(MailManager.class.getClassLoader());
            this.javaMailSender.send(
                mailMessageList.stream()
                    .map(mailResult -> this.convertToMimeMessage(mailResult, mailConfig))
                    .toArray(MimeMessage[]::new)
            );
            return mailMessageList;
        });
    }

    private MailConfig fetchMailConfig() {
        final MailConfig mailConfig = new MailConfig();
        mailConfig.setUsername(this.username);
        mailConfig.setSenderMail(this.senderMail);
        mailConfig.setPassword(this.password);
        mailConfig.setMailHost(this.mailHost);
        mailConfig.setMailPort(this.mailPort);
        mailConfig.setTlsEnabled(this.tlsEnabled);
        mailConfig.setEncoding(this.encoding);
        mailConfig.setSslProtocols(this.sslProtocols);
        return mailConfig;
    }

    private MimeMessage convertToMimeMessage(MailMessage mailResult, MailConfig mailConfig) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            messageHelper.setSubject(mailResult.getSubject());
            messageHelper.setText(mailResult.getContent(), true);
            messageHelper.setFrom(mailConfig.getSenderMail());
            messageHelper.setTo(this.toArray(mailResult.getReceiverMailAddresses()));
            messageHelper.setCc(this.toArray(mailResult.getCcMailAddresses()));
            messageHelper.setBcc(this.toArray(mailResult.getBccMailAddresses()));
            return messageHelper.getMimeMessage();
        } catch (MessagingException e) {
            throw new RuntimeException("MailMessage cannot converter to MimeMessage", e);
        }
    }

    private String[] toArray(Collection<String> val){
        if(val == null){
            return new String[0];
        }
        return val.toArray(String[]::new);

    }
}
