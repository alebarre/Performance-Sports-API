package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoLinkRequestDTO(
        @NotNull Long alunoId,
        @NotNull BigDecimal valor,
        @NotNull LocalDate dueDate,
        Long modalidadeId,
        Long eventoId
) {}
