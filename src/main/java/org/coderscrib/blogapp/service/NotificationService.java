package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.entity.Notification;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.repository.NotificationRepository;
import org.coderscrib.blogapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing notifications and sending email notifications.
 * This service creates notification records and triggers email sending.
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, 
                              UserRepository userRepository,
                              EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a notification and sends an email for user registration.
     * 
     * @param user The newly registered user
     */
    public void notifyUserRegistration(User user) {
        // Create notification record
        Notification notification = Notification.builder()
                .message("Welcome to BlogApp! Your account has been successfully created.")
                .receiver(user)
                .type(Notification.Type.REGISTRATION)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send email notification
        emailService.sendRegistrationEmail(user.getEmail(), user.getUsername());
    }

    /**
     * Creates a notification and sends an email for password change.
     * 
     * @param user The user who changed their password
     */
    public void notifyPasswordChange(User user) {
        // Create notification record
        Notification notification = Notification.builder()
                .message("Your password has been successfully changed.")
                .receiver(user)
                .type(Notification.Type.PASSWORD_CHANGE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send email notification
        emailService.sendPasswordChangeEmail(user.getEmail(), user.getUsername());
    }

    /**
     * Creates a notification and sends an email for profile update.
     * 
     * @param user The user who updated their profile
     */
    public void notifyProfileUpdate(User user) {
        // Create notification record
        Notification notification = Notification.builder()
                .message("Your profile information has been successfully updated.")
                .receiver(user)
                .type(Notification.Type.PROFILE_UPDATE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send email notification
        emailService.sendProfileUpdateEmail(user.getEmail(), user.getUsername());
    }

    /**
     * Creates a notification and sends an email when a post receives a like.
     * 
     * @param post The post that was liked
     * @param liker The user who liked the post
     */
    public void notifyPostLike(Post post, User liker) {
        User postAuthor = post.getAuthor();

        // Don't notify if the user likes their own post
        if (postAuthor.getId().equals(liker.getId())) {
            return;
        }

        // Create notification record
        String message = liker.getDisplayName() + " liked your post: \"" + 
                         (post.getTitle() != null ? post.getTitle() : "Untitled") + "\"";

        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.LIKE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send email notification
        String postTitle = post.getTitle() != null ? post.getTitle() : "Untitled";
        emailService.sendLikeNotificationEmail(
            postAuthor.getEmail(), 
            postAuthor.getUsername(), 
            liker.getDisplayName(), 
            postTitle
        );
    }

    /**
     * Creates a notification and sends an email when a post receives a comment.
     * 
     * @param post The post that was commented on
     * @param commenter The user who commented
     * @param commentContent The content of the comment
     */
    public void notifyPostComment(Post post, User commenter, String commentContent) {
        User postAuthor = post.getAuthor();

        // Don't notify if the user comments on their own post
        if (postAuthor.getId().equals(commenter.getId())) {
            return;
        }

        // Create notification record
        String message = commenter.getDisplayName() + " commented on your post: \"" + 
                         (post.getTitle() != null ? post.getTitle() : "Untitled") + "\"";

        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.COMMENT)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send email notification
        String postTitle = post.getTitle() != null ? post.getTitle() : "Untitled";
        String commentExcerpt = commentContent.length() > 100 
                ? commentContent.substring(0, 97) + "..." 
                : commentContent;

        emailService.sendCommentNotificationEmail(
            postAuthor.getEmail(), 
            postAuthor.getUsername(), 
            commenter.getDisplayName(), 
            postTitle,
            commentExcerpt
        );
    }
}
