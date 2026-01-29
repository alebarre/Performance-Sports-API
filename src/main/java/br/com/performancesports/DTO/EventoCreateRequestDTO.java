package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventoCreateRequestDTO(
        @NotBlank @Size(max = 240) String nomeDescricao,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        @NotNull LocalTime hora,
        @NotNull Long modalidadeId,
        @NotNull List<Long> alunoIds
) {}
