package com.pino.mailsample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder(setterPrefix = "set")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailMessage {

    protected String from;
    protected Set<String> receiverMailAddresses;
    protected String subject;
    protected String content;
    protected Set<String> ccMailAddresses;
    protected Set<String> bccMailAddresses;
}
