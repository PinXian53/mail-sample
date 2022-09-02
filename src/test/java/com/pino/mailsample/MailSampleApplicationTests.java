package com.pino.mailsample;

import com.pino.mailsample.manager.MailManager;
import com.pino.mailsample.manager.MailTemplateManager;
import com.pino.mailsample.model.MailMessage;
import com.pino.mailsample.model.MailTemplate;
import com.pino.mailsample.model.OrderInfo;
import com.pino.mailsample.model.WinTheLotteryNotifyModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
class MailSampleApplicationTests {

    private final String mailTitle = "Pino Shop 線上購物活動《P幣1000點》得獎通知";
    private final String mailContent = """
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml">
        	<head>
        		<style>
            .ttag>p,
            ttag>h1,
            ttag>h2,
            ttag>h3 {
                margin-top: 0;
            }
            </style>
        	</head>
        	<body style="margin: 0; padding: 0;">
        		<table style="width: 100%; border-collapse: collapse; text-align: left;" border="0">
        			<tbody>
        				<tr>
        					<td style="text-align: left; white-space: nowrap;" colspan="3">親愛的 ${userName} 您好：</td>
        					<td>&nbsp;</td>
        				</tr>
        				<tr>
        					<td style="width: 1%;">&nbsp;</td>
        					<td colspan="3">&nbsp;</td>
        				</tr>
        				<tr>
        					<td style="width: 1%;">&nbsp;</td>
        					<td style="text-align: left;" colspan="3">恭喜您參加 Pino shop 線上購物舉辦「滿$18888登記抽1000P幣」活動，獲得【P幣1000點】</td>
        				</tr>
        				<tr>
        					<td style="width: 1%;">&nbsp;</td>
        					<td style="text-align: left;" colspan="3">我們將於<span style="color:red">7日</span>後陸續送出獎品</td>
        				</tr>
        				<tr>
        					<td style="width: 1%;">&nbsp;</td>
        					<td colspan="3">&nbsp;</td>
        				</tr>
        				<tr>
        					<td style="width: 1%;">&nbsp;</td>
        					<td colspan="3">
        						<br/>
        						<table style="border-collapse: collapse; width: 100%; text-align: center;" border="1">
        							<tbody>
        								<tr>
        									<td style="font-weight: bold;">訂單編號</td>
        									<td style="font-weight: bold;">產品名稱</td>
        									<td style="font-weight: bold;">購買日期</td>
        								</tr>
        								<#list mailContentList as mailContent>
        								<tr>
        									<td>${mailContent.orderId}</td>
        									<td>${mailContent.productName}</td>
        									<td>${mailContent.purchaseDate}</td>
        								</tr>
        							</#list>
        						</tbody>
        					</table>
        				</td>
        			</tr>
        			<tr>
        				<td style="width: 1%;">&nbsp;</td>
        				<td colspan="3">&nbsp;</td>
        			</tr>
        			<tr>
        				<td style="width: 1%;">&nbsp;</td>
        				<td colspan="3">&nbsp;</td>
        			</tr>
        			<tr>
        				<td style="width: 1%;">&nbsp;</td>
        				<td style="text-align: left;" colspan="3">Pino shop 線上購物服務中心&nbsp;敬啟</td>
        			</tr>
        			<tr>
        				<td style="width: 1%;">&nbsp;</td>
        				<td colspan="3">&nbsp;</td>
        			</tr>
        			<tr>
        				<td style="width: 1%;">&nbsp;</td>
        				<td style="text-align: left;" colspan="3">※此信件為系統自動發出，請勿直接回覆。</td>
        			</tr>
        		</tbody>
        	</table>
        </body>
        </html>
        """;

    @Autowired MailTemplateManager mailTemplateManager;
    @Autowired MailManager mailManager;

    @Test
    void sendTestMail() throws InterruptedException {
        // 取得信件模板
        MailTemplate mailTemplate = new MailTemplate(mailTitle, mailContent);
        // 建立測試資料
        List<OrderInfo> orderInfoList = new ArrayList<>();
        orderInfoList.add(new OrderInfo("202209010001", "ROG Zephyrus M16", "2022/09/01"));
        orderInfoList.add(new OrderInfo("202209010002", "石頭掃地機器人", "2022/09/01"));
        WinTheLotteryNotifyModel winTheLotteryNotifyModel = new WinTheLotteryNotifyModel();
        winTheLotteryNotifyModel.setReceiverMailAddresses(Set.of("???@gmail.com"));
        winTheLotteryNotifyModel.setUserName("Pin");
        winTheLotteryNotifyModel.setMailContentList(orderInfoList);
        // 轉換成 MailMessage
        MailMessage mailMessage = mailTemplateManager.genMailMessageFromTemplate(winTheLotteryNotifyModel,
            mailTemplate);
        // 寄出信件
        mailManager.sendMailImmediately(List.of(mailMessage)).whenComplete((mailMessageList, exception) -> {
            if (exception != null) {
                System.out.println(exception.getMessage());
            }
        });

        Thread.sleep(30000);
    }

}
