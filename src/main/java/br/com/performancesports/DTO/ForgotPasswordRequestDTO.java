package br.com.performancesports.DTO;

import jakarta.validation.constraints.*;

public record ForgotPasswordRequestDTO(
        @Email @NotBlank String email
) {}
