package com.pino.mailsample.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class WinTheLotteryNotifyModel extends BaseMailDataModel {

    private String userName;
    private List<OrderInfo> mailContentList;

}
