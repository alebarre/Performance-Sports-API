package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfessorApproveRequestDTO(
        @NotNull Boolean approved,
        @NotBlank String role // ADMIN|USER
) {}
