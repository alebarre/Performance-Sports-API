package br.com.performancesports.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        @NotBlank @Pattern(regexp = "\\d{8}") String cep,
        @NotBlank @Size(max = 160) String logradouro,
        @NotBlank @Size(max = 20) String numero,
        @Size(max = 120) String complemento,
        @NotBlank @Size(max = 120) String bairro,
        @NotBlank @Size(max = 120) String cidade,
        @NotBlank @Size(min = 2, max = 2) String uf,
        @NotBlank @Size(max = 80) String pais
) {}
