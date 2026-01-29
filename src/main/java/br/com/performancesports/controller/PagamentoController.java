package br.com.performancesports.controller;

import br.com.performancesports.DTO.*;
import br.com.performancesports.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/links")
    public ResponseEntity<PagamentoLinkResponseDTO> createLink(@Valid @RequestBody PagamentoLinkRequestDTO req,
                                                              Authentication auth) {
        return ResponseEntity.status(201).body(pagamentoService.createLink(req, auth.getName()));
    }

    // opcional (blueprint)
    @GetMapping
    public ResponseEntity<List<PagamentoListItemDTO>> list(@RequestParam(value = "status", required = false) String status,
                                                          Authentication auth) {
        return ResponseEntity.ok(pagamentoService.list(status, auth.getName()));
    }

    @PostMapping("/webhook/pagseguro")
    public ResponseEntity<MessageResponseDTO> webhookPagSeguro(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pagamentoService.webhookPagSeguro(payload));
    }
}
