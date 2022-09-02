package com.pino.mailsample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class OrderInfo {
    private String orderId;
    private String productName;
    private String purchaseDate;
}
