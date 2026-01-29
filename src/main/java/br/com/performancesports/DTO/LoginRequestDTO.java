package br.com.performancesports.DTO;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
