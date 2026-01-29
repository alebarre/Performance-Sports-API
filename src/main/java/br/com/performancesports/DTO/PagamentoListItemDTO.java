package br.com.performancesports.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoListItemDTO(
        Long id,
        String status,
        BigDecimal valor,
        LocalDate dueDate,
        Long alunoId,
        Long professorId
) {}
