package com.pino.mailsample.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MailConfig {

    private String username;
    private String senderMail;
    private String password;
    private String mailHost;
    private Integer mailPort;
    private String encoding;
    private String tlsEnabled;
    private String sslProtocols;

}
