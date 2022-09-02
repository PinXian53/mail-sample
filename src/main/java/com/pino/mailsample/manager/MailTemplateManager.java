package com.pino.mailsample.manager;

import com.pino.mailsample.model.BaseMailDataModel;
import com.pino.mailsample.model.MailMessage;
import com.pino.mailsample.model.MailTemplate;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringWriter;

@Component
public class MailTemplateManager {

    private StringTemplateLoader templateLoader;
    private Configuration configuration;

    @PostConstruct
    public void init() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_29);
        this.templateLoader = new StringTemplateLoader();
        this.configuration.setTemplateLoader(this.templateLoader);
    }

    public MailMessage genMailMessageFromTemplate(BaseMailDataModel mailModel, MailTemplate mailTemplate) {
        String templateSubject = "_subject";
        this.templateLoader.putTemplate(templateSubject, mailTemplate.subject());
        String templateContent = "_content";
        this.templateLoader.putTemplate(templateContent, mailTemplate.content());

        try {
            StringWriter stringSubject = new StringWriter();
            Template subjectTemplate = this.configuration.getTemplate(templateSubject);
            subjectTemplate.process(mailModel, stringSubject);

            StringWriter stringContent = new StringWriter();
            Template contentTemplate = this.configuration.getTemplate(templateContent);
            contentTemplate.process(mailModel, stringContent);

            String subject = stringSubject.toString();
            String content = stringContent.toString();

            return MailMessage.builder()
                .setReceiverMailAddresses(mailModel.getReceiverMailAddresses())
                .setCcMailAddresses(mailModel.getCcMailAddresses())
                .setBccMailAddresses(mailModel.getBccMailAddresses())
                .setSubject(subject)
                .setContent(content)
                .build();
        } catch (IOException | TemplateException e) {
            throw new IllegalArgumentException("Can't parse template", e);
        }
    }

}
