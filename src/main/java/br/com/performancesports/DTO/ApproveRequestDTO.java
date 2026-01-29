package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotNull;

public record ApproveRequestDTO(
        @NotNull Boolean approved
) {}
