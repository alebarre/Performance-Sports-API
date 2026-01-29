package br.com.performancesports.controller;

import br.com.performancesports.DTO.AlunoDetailDTO;
import br.com.performancesports.DTO.AlunoListItemDTO;
import br.com.performancesports.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    public List<AlunoListItemDTO> listar(Authentication auth) {
        return alunoService.listar(auth.getName()); // auth.getName() = email (subject)
    }

    @GetMapping("/{id}")
    public AlunoDetailDTO buscar(@PathVariable Long id, Authentication auth) {
        return alunoService.buscar(id, auth.getName());
    }


@PostMapping("/{id}/approve")
public org.springframework.http.ResponseEntity<br.com.performancesports.DTO.MessageResponseDTO> approve(
        @PathVariable Long id,
        @jakarta.validation.Valid @RequestBody br.com.performancesports.DTO.ApproveRequestDTO req,
        org.springframework.security.core.Authentication auth
) {
    return org.springframework.http.ResponseEntity.ok(alunoService.approve(id, req, auth.getName()));
}

}
