package org.coderscrib.blogapp.service;

import jakarta.mail.MessagingException;
import org.coderscrib.blogapp.entity.Notification;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public void notifyUserRegistration(User user) throws MessagingException, UnsupportedEncodingException {
        Notification notification = Notification.builder()
                .message("Welcome to WriteCue! Your account has been successfully created.")
                .receiver(user)
                .type(Notification.Type.REGISTRATION)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        emailService.sendRegistrationEmail(user.getEmail(), user.getUsername());
    }

    public void notifyUserPasswordChange(User user) throws MessagingException, UnsupportedEncodingException {
        Notification notification = Notification.builder()
                .message("Your WriteCue password has been successfully changed.")
                .receiver(user)
                .type(Notification.Type.PASSWORD_CHANGE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        emailService.sendPasswordChangeEmail(user.getEmail(), user.getUsername());
    }

    public void notifyProfileUpdate(User user) throws MessagingException, UnsupportedEncodingException {
        Notification notification = Notification.builder()
                .message("Your profile has been successfully updated.")
                .receiver(user)
                .type(Notification.Type.PROFILE_UPDATE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        emailService.sendProfileUpdateEmail(user.getEmail(), user.getUsername());
    }

    public void notifyPostLike(Post post, User liker) throws MessagingException, UnsupportedEncodingException {
        User postAuthor = post.getAuthor();

        // Don't notify if user likes own post
        if (postAuthor.getId().equals(liker.getId())) return;

        String postTitle = getPostTitle(post);
        String message = String.format("%s liked your post: \"%s\"", liker.getDisplayName(), postTitle);

        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.LIKE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        emailService.sendLikeNotificationEmail(
                postAuthor.getEmail(),
                postAuthor.getUsername(),
                liker.getDisplayName(),
                postTitle
        );
    }

    public void notifyPostComment(Post post, User commenter, String commentContent) throws MessagingException, UnsupportedEncodingException {
        User postAuthor = post.getAuthor();

        // Don't notify if user comments on own post
        if (postAuthor.getId().equals(commenter.getId())) return;

        String postTitle = getPostTitle(post);
        String message = String.format("%s commented on your post: \"%s\"", commenter.getDisplayName(), postTitle);
        String commentExcerpt = getCommentExcerpt(commentContent);

        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.COMMENT)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        emailService.sendCommentNotificationEmail(
                postAuthor.getEmail(),
                postAuthor.getUsername(),
                commenter.getDisplayName(),
                postTitle,
                commentExcerpt
        );
    }

    // 🔁 Helpers

    private String getPostTitle(Post post) {
        return post.getTitle() != null ? post.getTitle() : "Untitled";
    }

    private String getCommentExcerpt(String comment) {
        return comment.length() > 100 ? comment.substring(0, 97) + "..." : comment;
    }
}
