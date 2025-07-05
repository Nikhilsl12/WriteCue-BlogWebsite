package org.coderscrib.blogapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String username;

    private String displayName;

    @Email(message = "Email is invalid")
    private String email;

    private String bio;

//    private String password; // Optional, for password updates
}
