package br.com.performancesports.DTO;

import jakarta.validation.constraints.*;

public record RegisterRequestDTO(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 120) String sobrenome
        // Somente acrescentar cpf/dataNascimento/endereco/modalidade aqui depois se necessário.
        // Regra: "não vazar sensível" seria em RESPONSE e não aqui.
) {}
