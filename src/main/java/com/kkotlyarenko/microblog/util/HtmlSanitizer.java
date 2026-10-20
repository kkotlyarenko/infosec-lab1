package com.kkotlyarenko.microblog.util;

import org.springframework.web.util.HtmlUtils;

/**
 * Утилита экранирования пользовательских данных перед отдачей в ответе API.
 *
 * <p>Защита от XSS (OWASP A03:2021). Даже если клиент вставит полученное значение
 * в HTML без обработки, опасные символы уже экранированы и не превратятся в
 * исполняемый код/теги.</p>
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    /** Экранирует один строковый фрагмент. */
    public static String escape(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }
}
