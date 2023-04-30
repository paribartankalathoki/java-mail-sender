package com.kalathoki.javamailsender.service;


import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author:- Paribartan Kalathoki
 * @created on:- 30 Apr, 2023 at 9:41 PM
 */

@Service
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    private static final String MAIL_TEMPLATE_BASE_NAME = "mail/MailMessage";
    private static final String MAIL_TEMPLATE_PREFIX = "/templates/";
    private static final String MAIL_TEMPLATE_SUFFIX = ".html";
    private static final String UTF_8 = "UTF-8";

    @Value("${spring.mail.username}")
    private String email;

    @Value("${to.mail.address}")
    private String toEmailAddress;

    private static TemplateEngine templateEngine;

    static {
        templateEngine = emailTemplateEngine();
    }

    private static TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setTemplateResolver(htmlTemplateResolver());
        springTemplateEngine.setTemplateEngineMessageSource(emailMessageSource());

        return springTemplateEngine;
    }

    private static ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(MAIL_TEMPLATE_PREFIX);
        templateResolver.setSuffix(MAIL_TEMPLATE_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8);
        templateResolver.setCacheable(false);

        return templateResolver;
    }

    private static ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();

        resourceBundleMessageSource.setBasename(MAIL_TEMPLATE_BASE_NAME);

        return  resourceBundleMessageSource;
    }

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(email);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        this.mailSender.send(mailMessage);

    }

    @Override
    public void sendTestMail() {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMultipart content = new MimeMultipart("related");

            // ContentID is used by both parts
            String cid = "logo.png";

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(email);
            helper.setTo(toEmailAddress);
            helper.setSubject("Development email testing");

            // HTML part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(createDynamicHtmlViewContent("mail-sender-test.html", null), "text/html");
            content.addBodyPart(textPart);

            // Image part
            MimeBodyPart imagePart = new MimeBodyPart();
            ClassPathResource dataResource = new ClassPathResource("img/logo.png");
            imagePart.attachFile(dataResource.getFile());
            imagePart.setContentID("<" + cid + ">");
            imagePart.setDisposition(Part.INLINE);
            content.addBodyPart(imagePart);

            mimeMessage.setContent(content);

            mailSender.send(mimeMessage);
            log.info("Mail sent successfully");
        } catch (Exception exception) {
            log.error("Error Occurs: message - {} ", exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    public String createDynamicHtmlViewContent(String template, Map<String, Object> details) {
        final Context context = new Context();

        context.setVariables(details);

        return templateEngine.process(template, context);
    }
}
