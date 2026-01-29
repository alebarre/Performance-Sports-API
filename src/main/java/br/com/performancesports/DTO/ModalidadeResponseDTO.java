package br.com.performancesports.DTO;

import java.math.BigDecimal;

public record ModalidadeResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor
) {}
