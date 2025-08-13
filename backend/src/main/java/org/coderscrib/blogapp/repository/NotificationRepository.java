package org.coderscrib.blogapp.repository;

import org.coderscrib.blogapp.entity.Notification;
import org.coderscrib.blogapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndIsReadFalse(User user);
}
