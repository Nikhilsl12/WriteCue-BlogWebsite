package org.coderscrib.blogapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users.
 * This service handles the actual sending of emails using Spring's JavaMailSender.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.sender}")
    private String sender;

    @Value("${app.email.sender-name}")
    private String senderName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple text email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param text The email body text
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    /**
     * Sends an HTML email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param htmlContent The email body as HTML
     * @throws MessagingException If there's an error creating or sending the message
     * @throws java.io.UnsupportedEncodingException If there's an encoding error
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) 
            throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(sender, senderName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Sends a registration confirmation email.
     * 
     * @param to The recipient's email address
     * @param username The user's username
     */
    public void sendRegistrationEmail(String to, String username) {
        String subject = "Welcome to BlogApp - Registration Confirmation";
        String text = "Hello " + username + ",\n\n" +
                "Thank you for registering with BlogApp. Your account has been successfully created.\n\n" +
                "You can now log in and start using our platform.\n\n" +
                "Best regards,\n" +
                "The BlogApp Team";

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Sends a password change confirmation email.
     * 
     * @param to The recipient's email address
     * @param username The user's username
     */
    public void sendPasswordChangeEmail(String to, String username) {
        String subject = "BlogApp - Password Change Confirmation";
        String text = "Hello " + username + ",\n\n" +
                "Your password has been successfully changed.\n\n" +
                "If you did not request this change, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "The BlogApp Team";

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Sends a profile update confirmation email.
     * 
     * @param to The recipient's email address
     * @param username The user's username
     */
    public void sendProfileUpdateEmail(String to, String username) {
        String subject = "BlogApp - Profile Update Confirmation";
        String text = "Hello " + username + ",\n\n" +
                "Your profile information has been successfully updated.\n\n" +
                "If you did not make these changes, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "The BlogApp Team";

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Sends a notification email when a user receives a like on their post.
     * 
     * @param to The recipient's email address
     * @param username The user's username
     * @param likerName The name of the user who liked the post
     * @param postTitle The title or excerpt of the post
     */
    public void sendLikeNotificationEmail(String to, String username, String likerName, String postTitle) {
        String subject = "BlogApp - New Like on Your Post";
        String text = "Hello " + username + ",\n\n" +
                likerName + " liked your post: \"" + postTitle + "\"\n\n" +
                "Log in to see more details.\n\n" +
                "Best regards,\n" +
                "The BlogApp Team";

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Sends a notification email when a user receives a comment on their post.
     * 
     * @param to The recipient's email address
     * @param username The user's username
     * @param commenterName The name of the user who commented
     * @param postTitle The title or excerpt of the post
     * @param commentExcerpt A short excerpt of the comment
     */
    public void sendCommentNotificationEmail(String to, String username, String commenterName, 
                                            String postTitle, String commentExcerpt) {
        String subject = "BlogApp - New Comment on Your Post";
        String text = "Hello " + username + ",\n\n" +
                commenterName + " commented on your post: \"" + postTitle + "\"\n\n" +
                "Comment: \"" + commentExcerpt + "\"\n\n" +
                "Log in to see the full comment and respond.\n\n" +
                "Best regards,\n" +
                "The BlogApp Team";

        sendSimpleEmail(to, subject, text);
    }
}
