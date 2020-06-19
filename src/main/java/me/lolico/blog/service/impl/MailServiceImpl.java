package me.lolico.blog.service.impl;

import me.lolico.blog.service.MailService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author lolico
 */
@Async
@Component
public class MailServiceImpl implements MailService, InitializingBean {
    private final JavaMailSender sender;
    @Value("${spring.mail.username}")
    private String from;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.sender = javaMailSender;
    }

    @Override
    public Future<Boolean> sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setText(to);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
        return new AsyncResult<>(true);
    }

    @Override
    public Future<Boolean> sendSimpleMessage(String to, String subject, String text, Object... args) {
        return sendSimpleMessage(to, subject, String.format(text, args));
    }

    @Override
    public Future<Boolean> sendHtmlMessage(String to, String subject, String text, Object... args) throws FileNotFoundException, MessagingException {
        return sendMessageWithAttachment(to, subject, String.format(text, args), true, null);
    }

    @Override
    public Future<Boolean> sendHtmlMessageWithAttachment(String to, String subject, String pathToAttachment, String text, Object... args) throws FileNotFoundException, MessagingException {
        return sendMessageWithAttachment(to, subject, String.format(text, args), true, pathToAttachment);
    }

    private Future<Boolean> sendMessageWithAttachment(String to, String subject, String text, boolean isHtml, String pathToAttachment) throws FileNotFoundException, MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, isHtml);
        if (pathToAttachment != null) {
            InputStreamResource inputStreamResource = new InputStreamResource(new BufferedInputStream(new FileInputStream(pathToAttachment)));
            helper.addAttachment(Objects.requireNonNull(inputStreamResource.getFilename()), inputStreamResource);
        }
        sender.send(mimeMessage);
        return new AsyncResult<>(true);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(StringUtils.hasText(from), "from is required");
    }
}
