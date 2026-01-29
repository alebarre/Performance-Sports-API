package br.com.performancesports.controller;

import br.com.performancesports.DTO.ModalidadeRequestDTO;
import br.com.performancesports.DTO.ModalidadeResponseDTO;
import br.com.performancesports.service.ModalidadeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modalidades")
public class ModalidadeController {

    private final ModalidadeService service;

    public ModalidadeController(ModalidadeService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ModalidadeResponseDTO> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public ModalidadeResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModalidadeResponseDTO criar(@RequestBody @Valid ModalidadeRequestDTO request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public ModalidadeResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid ModalidadeRequestDTO request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

