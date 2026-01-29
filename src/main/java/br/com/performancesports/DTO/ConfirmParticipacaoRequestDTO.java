package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotBlank;

public record ConfirmParticipacaoRequestDTO(
        @NotBlank String confirm // YES|NO
) {}
