package br.com.performancesports.controller;

import br.com.performancesports.DTO.*;
import br.com.performancesports.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping({"/register", "/register-aluno"})
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO req) {
        String code = authService.registerAluno(req); // <- agora retorna code (ou null)
        return ResponseEntity.accepted()
                .body(new MessageResponseDTO("Se aplicável, enviamos um código para seu e-mail.", code));
    }

    @PostMapping("/confirm-register")
    public ResponseEntity<MessageResponseDTO> confirmRegister(@Valid @RequestBody ConfirmTokenRequestDTO req) {
        authService.confirmRegister(req);
        return ResponseEntity.ok(new MessageResponseDTO("E-mail confirmado. Aguarde aprovação do professor responsável."));
    }

    /**
     * Plano B: valida código de reset (PASSWORD_RESET) sem side-effect.
     * (não altera emailVerified e não muda senha aqui)
     */
    @PostMapping("/confirm")
    public ResponseEntity<MessageResponseDTO> confirm(@Valid @RequestBody ConfirmTokenRequestDTO req) {
        authService.confirm(req);
        return ResponseEntity.ok(new MessageResponseDTO("Código validado com sucesso."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping({"/forgot", "/forgot-password"})
    public ResponseEntity<MessageResponseDTO> forgot(@Valid @RequestBody ForgotPasswordRequestDTO req) {
        String code = authService.forgot(req); // <- agora retorna code (ou null)
        return ResponseEntity.ok(new MessageResponseDTO("Se aplicável, enviamos um código para seu e-mail.", code));
    }

    @PostMapping({"/reset", "/reset-password"})
    public ResponseEntity<MessageResponseDTO> reset(@Valid @RequestBody ResetPasswordRequestDTO req) {
        authService.reset(req);
        return ResponseEntity.ok(new MessageResponseDTO("Senha alterada com sucesso."));
    }
}
