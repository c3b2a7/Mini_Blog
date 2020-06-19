package me.lolico.blog.service;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.util.concurrent.Future;

public interface MailService {
    Future<Boolean> sendSimpleMessage(String to, String subject, String text);

    Future<Boolean> sendSimpleMessage(String to, String subject, String text, Object... args);

    Future<Boolean> sendHtmlMessage(String to, String subject, String text, Object... args) throws FileNotFoundException, MessagingException;

    Future<Boolean> sendHtmlMessageWithAttachment(String to, String subject, String pathToAttachment, String text, Object... args) throws FileNotFoundException, MessagingException;
}
