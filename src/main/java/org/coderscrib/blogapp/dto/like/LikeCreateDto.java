package org.coderscrib.blogapp.dto.like;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCreateDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long postId;
}
