package com.kkotlyarenko.microblog.dto;

/**
 * Ответ на успешную аутентификацию: JWT access-токен и метаданные.
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username
) {
    public AuthResponse(String accessToken, long expiresIn, String username) {
        this(accessToken, "Bearer", expiresIn, username);
    }
}
