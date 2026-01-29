package br.com.performancesports.DTO;

public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String role,
        String accountType
) {}