package br.com.performancesports.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmTokenRequestDTO(
        @Email @NotBlank String email,

        @NotBlank
        @JsonAlias({"code", "token"})
        String token
) {}
