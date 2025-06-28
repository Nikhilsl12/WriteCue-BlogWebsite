package org.coderscrib.blogapp.dto.like;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}
