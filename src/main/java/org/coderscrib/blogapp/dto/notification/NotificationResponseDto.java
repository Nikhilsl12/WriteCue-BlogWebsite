package org.coderscrib.blogapp.dto.notification;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String message;
    private Long receiverId;
    private String type;
    private LocalDateTime createdAt;
    private boolean isRead;
}
