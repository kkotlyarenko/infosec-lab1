package com.kkotlyarenko.microblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Запрос на регистрацию нового пользователя.
 */
public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 64, message = "username must be 3..64 characters")
        @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "username contains invalid characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 128, message = "password must be 8..128 characters")
        String password
) {
}
