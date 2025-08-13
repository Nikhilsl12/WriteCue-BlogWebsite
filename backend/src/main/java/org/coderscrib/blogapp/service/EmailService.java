package org.coderscrib.blogapp.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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

    public void sendHtmlTemplate(String to, String subject, String templateName, Map<String, Object> variables) {
        logger.info("Preparing to send email to: {} with subject: '{}'", to, subject);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (templateName == null || templateName.isBlank()) {
            logger.error("Failed to send email: Template name is null or empty");
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        
        try {
            logger.debug("Creating email context with {} variables", variables != null ? variables.size() : 0);
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }
            
            logger.debug("Processing template: {}", templateName);
            String body = templateEngine.process("emails/" + templateName, context);
            
            logger.debug("Creating MIME message");
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            
            logger.debug("Setting email headers: from={}, to={}", sender, to);
            helper.setFrom(new InternetAddress(sender, senderName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setReplyTo(new InternetAddress(sender, senderName));
            
            logger.debug("Sending email");
            javaMailSender.send(msg);
            logger.info("Email sent successfully to: {} with subject: '{}'", to, subject);
            
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to send email due to encoding error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email due to encoding error", e);
        } catch (Exception e) {
            logger.error("Failed to send email to: {} with subject: '{}': {}", to, subject, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendRegistrationEmail(String to, String username) {
        logger.info("Sending registration email to: {}", to);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send registration email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (username == null || username.isBlank()) {
            logger.warn("Username is null or empty for registration email, using empty string");
            username = "";
        }
        
        logger.debug("Preparing registration email template with username: {}", username);
        sendHtmlTemplate(to, "Welcome to WriteCue!", "welcome.html", Map.of("username", username));
        logger.debug("Registration email template processing completed");
    }

    public void sendPasswordChangeEmail(String to, String username) {
        logger.info("Sending password change email to: {}", to);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send password change email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (username == null || username.isBlank()) {
            logger.warn("Username is null or empty for password change email, using empty string");
            username = "";
        }
        
        logger.debug("Preparing password change email template with username: {}", username);
        sendHtmlTemplate(to, "Your WriteCue Password Was Changed", "password-change.html", Map.of("username", username));
        logger.debug("Password change email template processing completed");
    }

    public void sendProfileUpdateEmail(String to, String username) {
        logger.info("Sending profile update email to: {}", to);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send profile update email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (username == null || username.isBlank()) {
            logger.warn("Username is null or empty for profile update email, using empty string");
            username = "";
        }
        
        logger.debug("Preparing profile update email template with username: {}", username);
        sendHtmlTemplate(to, "Your WriteCue Profile Was Updated", "profile-update.html", Map.of("username", username));
        logger.debug("Profile update email template processing completed");
    }

    public void sendLikeNotificationEmail(String to, String username, String likerName, String postTitle) {
        logger.info("Sending like notification email to: {}", to);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send like notification email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (username == null || username.isBlank()) {
            logger.warn("Username is null or empty for like notification email, using empty string");
            username = "";
        }
        
        if (likerName == null || likerName.isBlank()) {
            logger.warn("Liker name is null or empty for like notification email, using 'Someone'");
            likerName = "Someone";
        }
        
        if (postTitle == null || postTitle.isBlank()) {
            logger.warn("Post title is null or empty for like notification email, using 'your post'");
            postTitle = "your post";
        }
        
        logger.debug("Preparing like notification email template with username: {}, likerName: {}, postTitle: {}", 
                username, likerName, postTitle);
        sendHtmlTemplate(to, "New Like on Your Post - WriteCue", "like-notification.html",
                Map.of("username", username, "likerName", likerName, "postTitle", postTitle));
        logger.debug("Like notification email template processing completed");
    }

    public void sendCommentNotificationEmail(String to, String username, String commenterName, String postTitle, String commentExcerpt) {
        logger.info("Sending comment notification email to: {}", to);
        
        if (to == null || to.isBlank()) {
            logger.error("Failed to send comment notification email: Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        if (username == null || username.isBlank()) {
            logger.warn("Username is null or empty for comment notification email, using empty string");
            username = "";
        }
        
        if (commenterName == null || commenterName.isBlank()) {
            logger.warn("Commenter name is null or empty for comment notification email, using 'Someone'");
            commenterName = "Someone";
        }
        
        if (postTitle == null || postTitle.isBlank()) {
            logger.warn("Post title is null or empty for comment notification email, using 'your post'");
            postTitle = "your post";
        }
        
        if (commentExcerpt == null || commentExcerpt.isBlank()) {
            logger.warn("Comment excerpt is null or empty for comment notification email, using empty string");
            commentExcerpt = "";
        }
        
        logger.debug("Preparing comment notification email template with username: {}, commenterName: {}, postTitle: {}", 
                username, commenterName, postTitle);
        sendHtmlTemplate(to, "New Comment on Your Post - WriteCue", "comment-notification.html",
                Map.of("username", username, "commenterName", commenterName, "postTitle", postTitle, "commentExcerpt", commentExcerpt));
        logger.debug("Comment notification email template processing completed");
    }
}
