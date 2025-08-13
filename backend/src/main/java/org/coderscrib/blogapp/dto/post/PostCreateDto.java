package org.coderscrib.blogapp.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {
    @NotBlank
    @Size(max = 255)
    private String title;
    @NotBlank
    @Size(max = 10000)
    private String content;
    @NotNull
    private Long authorId;
}
