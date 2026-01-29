package br.com.performancesports.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProfessorRegisterRequestDTO(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 120) String sobrenome,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull LocalDate dataNascimento,
        @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
        @NotBlank String pagamentoFormato, // AULA|PERCENT
        BigDecimal pagamentoPercentual,
        List<Long> modalidadeIds,
        List<EnderecoRequestDTO> enderecos
) {}
