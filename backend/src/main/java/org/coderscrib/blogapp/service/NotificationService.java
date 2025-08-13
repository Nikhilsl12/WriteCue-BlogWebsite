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
        logger.info("Creating registration notification for user ID: {}", user.getId());
        
        if (user == null) {
            logger.error("Failed to create notification: User is null");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.debug("Building registration notification for user: {}", user.getUsername());
        Notification notification = Notification.builder()
                .message("Welcome to WriteCue! Your account has been successfully created.")
                .receiver(user)
                .type(Notification.Type.REGISTRATION)
                .isRead(false)
                .build();

        logger.debug("Saving registration notification to database");
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Registration notification created successfully: ID {}, for user: {}", 
                savedNotification.getId(), user.getUsername());
        
        logger.debug("Sending registration email to: {}", user.getEmail());
        emailService.sendRegistrationEmail(user.getEmail(), user.getUsername());
        logger.debug("Registration email sent successfully to: {}", user.getEmail());
    }

    public void notifyUserPasswordChange(User user) {
        logger.info("Creating password change notification for user ID: {}", user.getId());
        
        if (user == null) {
            logger.error("Failed to create password change notification: User is null");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.debug("Building password change notification for user: {}", user.getUsername());
        Notification notification = Notification.builder()
                .message("Your WriteCue password has been successfully changed.")
                .receiver(user)
                .type(Notification.Type.PASSWORD_CHANGE)
                .isRead(false)
                .build();

        logger.debug("Saving password change notification to database");
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Password change notification created successfully: ID {}, for user: {}", 
                savedNotification.getId(), user.getUsername());
        
        logger.debug("Sending password change email to: {}", user.getEmail());
        emailService.sendPasswordChangeEmail(user.getEmail(), user.getUsername());
        logger.debug("Password change email sent successfully to: {}", user.getEmail());
    }

    public void notifyProfileUpdate(User user) {
        logger.info("Creating profile update notification for user ID: {}", user.getId());
        
        if (user == null) {
            logger.error("Failed to create profile update notification: User is null");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.debug("Building profile update notification for user: {}", user.getUsername());
        Notification notification = Notification.builder()
                .message("Your profile has been successfully updated.")
                .receiver(user)
                .type(Notification.Type.PROFILE_UPDATE)
                .isRead(false)
                .build();

        logger.debug("Saving profile update notification to database");
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Profile update notification created successfully: ID {}, for user: {}", 
                savedNotification.getId(), user.getUsername());
        
        logger.debug("Sending profile update email to: {}", user.getEmail());
        emailService.sendProfileUpdateEmail(user.getEmail(), user.getUsername());
        logger.debug("Profile update email sent successfully to: {}", user.getEmail());
    }

    public void notifyPostLike(Post post, User liker) {
        logger.info("Creating post like notification for post ID: {} by user ID: {}", post.getId(), liker.getId());
        
        if (post == null || liker == null) {
            logger.error("Failed to create like notification: Post or User is null");
            throw new IllegalArgumentException("Post and User cannot be null");
        }
        
        User postAuthor = post.getAuthor();
        logger.debug("Post author is user ID: {}", postAuthor.getId());

        // Don't notify if user likes own post
        if (postAuthor.getId().equals(liker.getId())) {
            logger.debug("Skipping notification: User liked their own post");
            return;
        }

        String postTitle = getPostTitle(post);
        logger.debug("Creating notification for post: \"{}\"", postTitle);
        
        String message = String.format("%s liked your post: \"%s\"", liker.getDisplayName(), postTitle);
        logger.debug("Building like notification with message: {}", message);
        
        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.LIKE)
                .isRead(false)
                .build();

        logger.debug("Saving like notification to database");
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Like notification created successfully: ID {}, for user: {}", 
                savedNotification.getId(), postAuthor.getUsername());

        logger.debug("Sending like notification email to: {}", postAuthor.getEmail());
        emailService.sendLikeNotificationEmail(
                postAuthor.getEmail(),
                postAuthor.getUsername(),
                liker.getDisplayName(),
                postTitle
        );
        logger.debug("Like notification email sent successfully to: {}", postAuthor.getEmail());
    }

    public void notifyPostComment(Post post, User commenter, String commentContent) {
        logger.info("Creating post comment notification for post ID: {} by user ID: {}", post.getId(), commenter.getId());
        
        if (post == null || commenter == null) {
            logger.error("Failed to create comment notification: Post or User is null");
            throw new IllegalArgumentException("Post and User cannot be null");
        }
        
        if (commentContent == null) {
            logger.warn("Comment content is null, using empty string instead");
            commentContent = "";
        }
        
        User postAuthor = post.getAuthor();
        logger.debug("Post author is user ID: {}", postAuthor.getId());

        // Don't notify if user comments on own post
        if (postAuthor.getId().equals(commenter.getId())) {
            logger.debug("Skipping notification: User commented on their own post");
            return;
        }

        String postTitle = getPostTitle(post);
        logger.debug("Creating notification for post: \"{}\"", postTitle);
        
        String message = String.format("%s commented on your post: \"%s\"", commenter.getDisplayName(), postTitle);
        String commentExcerpt = getCommentExcerpt(commentContent);
        logger.debug("Comment excerpt: \"{}\"", commentExcerpt);
        
        logger.debug("Building comment notification with message: {}", message);
        Notification notification = Notification.builder()
                .message(message)
                .receiver(postAuthor)
                .type(Notification.Type.COMMENT)
                .isRead(false)
                .build();

        logger.debug("Saving comment notification to database");
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Comment notification created successfully: ID {}, for user: {}", 
                savedNotification.getId(), postAuthor.getUsername());

        logger.debug("Sending comment notification email to: {}", postAuthor.getEmail());
        emailService.sendCommentNotificationEmail(
                postAuthor.getEmail(),
                postAuthor.getUsername(),
                commenter.getDisplayName(),
                postTitle,
                commentExcerpt
        );
        logger.debug("Comment notification email sent successfully to: {}", postAuthor.getEmail());
    }
    // marking as read methods
    public void markAsRead(Long id){
        logger.info("Attempting to mark notification as read: ID {}", id);
        
        if (id == null) {
            logger.error("Failed to mark notification as read: ID is null");
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        
        logger.debug("Retrieving notification with ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Mark as read failed: Notification not found with ID: {}", id);
                    return ResourceNotFoundException.create("Notification", "id", id);
                });
                
        logger.debug("Setting notification as read: ID {}", id);
        notification.setRead(true);
        
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification marked as read successfully: ID {}, by user: {}", 
                savedNotification.getId(), notification.getReceiver().getUsername());
    }
    public void markAllAsRead(User user){
        logger.info("Attempting to mark all notifications as read for user ID: {}", user.getId());
        
        if (user == null) {
            logger.error("Failed to mark notifications as read: User is null");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.debug("Retrieving unread notifications for user: {}", user.getUsername());
        List<Notification> notificationList = notificationRepository.findByReceiverAndIsReadFalse(user);
        
        if (notificationList.isEmpty()) {
            logger.debug("No unread notifications found for user: {}", user.getUsername());
            return;
        }
        
        logger.debug("Found {} unread notifications for user: {}", notificationList.size(), user.getUsername());
        for(Notification n: notificationList){
            logger.debug("Setting notification ID: {} as read", n.getId());
            n.setRead(true);
        }
        
        notificationRepository.saveAll(notificationList);
        logger.info("All notifications marked as read successfully for user: {}, count: {}", 
                user.getUsername(), notificationList.size());
    }

    // 🔁 Helpers

    private String getPostTitle(Post post) {
        logger.debug("Getting post title");
        
        if (post == null) {
            logger.warn("Post is null when getting title, returning 'Untitled'");
            return "Untitled";
        }
        
        String title = post.getTitle() != null ? post.getTitle() : "Untitled";
        logger.debug("Post title: \"{}\"", title);
        return title;
    }

    private String getCommentExcerpt(String comment) {
        logger.debug("Creating comment excerpt");
        
        if (comment == null) {
            logger.warn("Comment is null when creating excerpt, returning empty string");
            return "";
        }
        
        String excerpt = comment.length() > 100 ? comment.substring(0, 97) + "..." : comment;
        logger.debug("Comment excerpt created, length: {}", excerpt.length());
        return excerpt;
    }

}
