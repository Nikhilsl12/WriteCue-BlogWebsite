package org.coderscrib.blogapp.dto.notification;

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
public class NotificationCreateDto {
    @NotBlank
    @Size(max = 255)
    private String message;

    @NotNull
    private Long receiverId;

    @NotBlank
    private String type;
}
