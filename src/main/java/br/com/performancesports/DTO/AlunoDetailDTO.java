package br.com.performancesports.DTO;

public record AlunoDetailDTO(
        Long id,
        String nome,
        String sobrenome,
        String cpfMascarado,
        boolean approved,
        boolean active,
        Long professorId
) {}