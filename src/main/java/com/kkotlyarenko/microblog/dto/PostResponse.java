package com.kkotlyarenko.microblog.dto;

/**
 * Представление поста в ответе API.
 * Все строковые поля уже прошли output-encoding (защита от XSS).
 */
public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        String createdAt
) {
}
