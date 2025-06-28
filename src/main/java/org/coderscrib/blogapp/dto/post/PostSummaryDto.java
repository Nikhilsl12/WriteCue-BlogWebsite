package org.coderscrib.blogapp.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
