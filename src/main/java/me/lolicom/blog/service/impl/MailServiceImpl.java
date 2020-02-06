package me.lolicom.blog.service.impl;

import me.lolicom.blog.service.MailService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lolicom
 */
@Component
public class MailServiceImpl implements InitializingBean, MailService {
    @Value("${spring.mail.username}")
    private String from;
    
    private final JavaMailSender sender;
    
    private ExecutorService executor;
    
    public MailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }
    
    protected Future<Boolean> send(SimpleMailMessage... mail) {
        return executor.submit(() -> sender.send(mail), true);
    }
    
    protected Future<Boolean> send(MimeMessage... mimeMessage) {
        return executor.submit(() -> sender.send(mimeMessage), true);
    }
    
    protected Future<Boolean> send(MimeMessagePreparator... mimeMessagePreparators) {
        return executor.submit(() -> sender.send(mimeMessagePreparators), true);
    }
    
    @Override
    public Future<Boolean> sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setText(to);
        message.setSubject(subject);
        message.setText(text);
        return send(message);
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
    
    protected Future<Boolean> sendMessageWithAttachment(String to, String subject, String text, boolean isHtml, String pathToAttachment) throws FileNotFoundException, MessagingException {
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
        return send(mimeMessage);
    }
    
    @Override
    public void afterPropertiesSet() {
        executor = Executors.newSingleThreadExecutor();
        if (StringUtils.isEmpty(from)) {
            from = "leisureee@163.com";
        }
    }
}
