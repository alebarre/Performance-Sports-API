package br.com.performancesports.controller;

import br.com.performancesports.DTO.*;
import br.com.performancesports.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<EventoCreateResponseDTO> create(@Valid @RequestBody EventoCreateRequestDTO req,
                                                         Authentication auth) {
        return ResponseEntity.status(201).body(eventoService.create(req, auth.getName()));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<MessageResponseDTO> approve(@PathVariable Long id,
                                                      @Valid @RequestBody ApproveRequestDTO req,
                                                      Authentication auth) {
        return ResponseEntity.ok(eventoService.approve(id, req, auth.getName()));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<MessageResponseDTO> confirm(@PathVariable Long id,
                                                      @Valid @RequestBody ConfirmParticipacaoRequestDTO req,
                                                      Authentication auth) {
        return ResponseEntity.ok(eventoService.confirm(id, req, auth.getName()));
    }
}
