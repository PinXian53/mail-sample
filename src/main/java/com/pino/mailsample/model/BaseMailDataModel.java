package com.pino.mailsample.model;

import lombok.Data;

import java.util.Set;

@Data
public abstract class BaseMailDataModel {
    protected Set<String> receiverMailAddresses;
    protected Set<String> ccMailAddresses;
    protected Set<String> bccMailAddresses;
}
