package org.coderscrib.blogapp.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentSummaryDto {
    private String displayName;
    private String content;
    private LocalDateTime createdAt;
}
