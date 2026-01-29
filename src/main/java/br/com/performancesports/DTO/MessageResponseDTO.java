package br.com.performancesports.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageResponseDTO(String message, String code) {

    public MessageResponseDTO(String message) {
        this(message, null);
    }
}
