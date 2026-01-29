package br.com.performancesports.domain.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiErrorDTO(
        int status,
        String error,
        String message,
        OffsetDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public static ApiErrorDTO of(int status, String error, String message) {
        return new ApiErrorDTO(status, error, message, OffsetDateTime.now(), null);
    }

    public static ApiErrorDTO of(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ApiErrorDTO(status, error, message, OffsetDateTime.now(), fieldErrors);
    }
}
