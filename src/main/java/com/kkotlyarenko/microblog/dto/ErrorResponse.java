package com.kkotlyarenko.microblog.dto;

/**
 * Единый ответ об ошибке. Не раскрывает внутренние детали (stack trace, SQL и т.п.).
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path
) {
}
