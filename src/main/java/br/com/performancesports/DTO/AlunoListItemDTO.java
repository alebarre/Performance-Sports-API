package br.com.performancesports.DTO;

public record AlunoListItemDTO(
        Long id,
        String nome,
        String sobrenome,
        boolean approved,
        boolean active,
        Long professorId
) {}
