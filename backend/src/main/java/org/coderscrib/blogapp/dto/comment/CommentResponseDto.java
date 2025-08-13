package org.coderscrib.blogapp.dto.comment;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long userId;
    private Long postId;
    private String displayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
