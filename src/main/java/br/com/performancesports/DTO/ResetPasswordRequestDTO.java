package br.com.performancesports.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @Email @NotBlank String email,

        @NotBlank
        @JsonAlias({"code", "token"})
        String token,

        @NotBlank @Size(min = 8, max = 72) String newPassword
) {}
