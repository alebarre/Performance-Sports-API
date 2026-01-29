package br.com.performancesports.controller;

import br.com.performancesports.DTO.*;
import br.com.performancesports.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody ProfessorRegisterRequestDTO req) {
        return ResponseEntity.accepted().body(professorService.register(req));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<MessageResponseDTO> approve(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorApproveRequestDTO req,
            Authentication auth
    ) {
        return ResponseEntity.ok(professorService.approve(id, req, auth.getName()));
    }
}
