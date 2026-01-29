package br.com.performancesports.DTO;

import java.time.LocalDate;

public record PagamentoLinkResponseDTO(
        Long id,
        String status,
        LocalDate dueDate,
        String paymentUrl
) {}
