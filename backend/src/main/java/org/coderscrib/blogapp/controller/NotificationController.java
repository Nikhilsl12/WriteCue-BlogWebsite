package org.coderscrib.blogapp.controller;

import org.coderscrib.blogapp.service.NotificationService;
import org.coderscrib.blogapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(){
        // need to implement later with getCurrent User after implementing spring security
        return ResponseEntity.ok().build();
    }
}
