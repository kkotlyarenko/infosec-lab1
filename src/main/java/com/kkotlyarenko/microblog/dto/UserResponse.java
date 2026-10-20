package com.kkotlyarenko.microblog.dto;

/**
 * Публичное представление пользователя для GET /api/data.
 * Пароль/хэш пароля сюда не попадает ни при каких условиях.
 */
public record UserResponse(
        Long id,
        String username,
        String role,
        String createdAt
) {
}
