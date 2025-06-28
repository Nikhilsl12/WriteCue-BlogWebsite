package org.coderscrib.blogapp.dto.comment;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    @NotBlank
    @Size(max = 1000)
    private String content;

}
