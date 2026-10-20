package com.kkotlyarenko.microblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на создание поста.
 */
public record PostRequest(
        @NotBlank(message = "title is required")
        @Size(min = 1, max = 200, message = "title must be 1..200 characters")
        String title,

        @NotBlank(message = "content is required")
        @Size(min = 1, max = 2000, message = "content must be 1..2000 characters")
        String content
) {
}
