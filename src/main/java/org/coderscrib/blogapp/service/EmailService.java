package org.coderscrib.blogapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.sender}")
    private String sender;

    @Value("${app.email.sender-name}")
    private String senderName;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }


    public void sendHtmlTemplate(String to, String subject, String templateName, Map<String,Object> variable) throws MessagingException, UnsupportedEncodingException {
        // preparing the thymeleaf context
        Context context = new Context();
        context.setVariables(variable);
        // Process the HTML template
        String body = templateEngine.process("emails/"+templateName, context);
        // Prepare the email
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setFrom(new InternetAddress(sender, senderName));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body,true);
        helper.setReplyTo(new InternetAddress(sender, senderName));

        // send the email
        javaMailSender.send(msg);
    }
    public void sendRegistrationEmail(String to, String username) throws MessagingException, UnsupportedEncodingException {
        sendHtmlTemplate(to,"Welcome to WriteCue!", "welcome.html", Map.of("username", username));
    }
    public void sendPasswordChangeEmail(String to, String username) throws MessagingException, UnsupportedEncodingException {
        sendHtmlTemplate(to,"Your WriteCue Password Was Changed", "password-change.html", Map.of("username", username));
    }
    public void sendProfileUpdateEmail(String to, String username) throws MessagingException, UnsupportedEncodingException {
        sendHtmlTemplate(to, "Your WriteCue Profile Was Updated", "profile-update.html",Map.of("username", username));
    }
    public void sendLikeNotificationEmail(String to, String username, String likerName, String postTitle) throws MessagingException, UnsupportedEncodingException {
        sendHtmlTemplate(to,"New Like on Your Post - WriteCue","like-notification.html",
                Map.of(
                        "username", username,
                        "likerName", likerName,
                        "postTitle", postTitle
                ));
    }
    public void sendCommentNotificationEmail(String to, String username, String commenterName,
                                             String postTitle, String commentExcerpt) throws MessagingException, UnsupportedEncodingException {
        sendHtmlTemplate(to, "New Comment on Your Post - WriteCue", "comment-notification.html",
                Map.of(
                        "username", username,
                        "commenterName", commenterName,
                        "postTitle",postTitle,
                        "commentExcerpt", commentExcerpt
                ));
    }
}
