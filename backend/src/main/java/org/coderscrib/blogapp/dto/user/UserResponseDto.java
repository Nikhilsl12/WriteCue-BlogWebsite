package org.coderscrib.blogapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String bio;
    private LocalDateTime createdAt;
    private List<PostSummaryDto> posts;

}
