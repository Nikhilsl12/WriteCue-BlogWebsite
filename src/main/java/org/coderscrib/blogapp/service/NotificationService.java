package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.entity.Notification;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.NotificationRepository;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public void notifyUserRegistration(User user) {
        Notification notification = Notification.builder()
                .message("Welcome to WriteCue! Your account has been successfully created.")
                .receiver(user)
                .type(Notification.Type.REGISTRATION)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        logger.info("Notification sent for registration of user with id {}", user.getId());
        emailService.sendRegistrationEmail(user.getEmail(), user.getUsername());
    }

    public void notifyUserPasswordChange(User user) {
        Notification notification = Notification.builder()
                .message("Your WriteCue password has been successfully changed.")
                .receiver(user)
                .type(Notification.Type.PASSWORD_CHANGE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        logger.info("Notification sent for password change of user with id {}", user.getId());
        emailService.sendPasswordChangeEmail(user.getEmail(), user.getUsername());
    }

    public void notifyProfileUpdate(User user) {
        Notification notification = Notification.builder()
                .message("Your profile has been successfully updated.")
                .receiver(user)
                .type(Notification.Type.PROFILE_UPDATE)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        logger.info("Notification sent for profile update of user with id {}", user.getId());
        emailService.sendProfileUpdateEmail(user.getEmail(), user.getUsername());
    }

    public void notifyPostLike(Post post, User liker) {
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
        logger.info("Notification sent for like of post with id {}", post.getId());
    }

    public void notifyPostComment(Post post, User commenter, String commentContent) {
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
        logger.info("Notification sent for comment on post with id {}", post.getId());
    }
    // marking as read methods
    public void markAsRead(Long id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.create("Notification", "id", id));
        notification.setRead(true);
        notificationRepository.save(notification);
        logger.info("User {} marked notification {} as read", notification.getReceiver().getUsername(), id);
    }
    public void markAllAsRead(User user){
        List<Notification> notificationList= notificationRepository.findByReceiverAndIsReadFalse(user);
        for(Notification n: notificationList){
            n.setRead(true);
        }
        notificationRepository.saveAll(notificationList);
        logger.info("User {} marked all notifications as read", user.getUsername());
    }

    // 🔁 Helpers

    private String getPostTitle(Post post) {
        return post.getTitle() != null ? post.getTitle() : "Untitled";
    }

    private String getCommentExcerpt(String comment) {
        return comment.length() > 100 ? comment.substring(0, 97) + "..." : comment;
    }

}
