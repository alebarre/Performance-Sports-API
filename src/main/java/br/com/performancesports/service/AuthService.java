package br.com.performancesports.service;

import br.com.performancesports.DTO.*;
import br.com.performancesports.domain.exception.AccountStateException;
import br.com.performancesports.domain.token.TokenType;
import br.com.performancesports.domain.user.AccountType;
import br.com.performancesports.domain.user.Role;
import br.com.performancesports.domain.user.User;
import br.com.performancesports.repository.UserRepository;
import br.com.performancesports.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${app.auth.exposeTokens:false}")
    private boolean exposeTokens;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * @return code (somente se exposeTokens=true), senão null
     */
    public String registerAluno(RegisterRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        // resposta neutra: não revelar existência
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return null;
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(req.password()))
                .enabled(false)
                .emailVerified(false)
                .role(Role.USER)
                .accountType(AccountType.ALUNO)
                .build();

        userRepository.save(user);

        var generated = tokenService.generate(email, TokenType.REGISTER_VERIFY);

        // TODO: enviar e-mail com generated.rawToken()
        return exposeTokens ? generated.rawToken() : null;
    }

    public void confirmRegister(ConfirmTokenRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        tokenService.assertValidOrThrow(email, TokenType.REGISTER_VERIFY, req.token());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        user.setEmailVerified(true);
        // enabled continua false até aprovação do professor
        userRepository.save(user);
    }

    /**
     * Plano B: só valida token de reset (PASSWORD_RESET) sem side-effect.
     */
    public void confirm(ConfirmTokenRequestDTO req) {
        String email = req.email().trim().toLowerCase();
        tokenService.assertValidOnly(email, TokenType.PASSWORD_RESET, req.token());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("invalid"));

        if (!user.isEmailVerified()) {
            throw AccountStateException.emailNotVerified();
        }
        if (!user.isEnabled()) {
            throw AccountStateException.pendingApproval();
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getId(),
                String.valueOf(user.getRole()),
                String.valueOf(user.getAccountType())
        );
    }

    /**
     * @return code (somente se exposeTokens=true), senão null
     */
    public String forgot(ForgotPasswordRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        // resposta neutra
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            return null;
        }

        var generated = tokenService.generate(email, TokenType.PASSWORD_RESET);

        // TODO: enviar e-mail com generated.rawToken()
        return exposeTokens ? generated.rawToken() : null;
    }

    public void reset(ResetPasswordRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        tokenService.assertValidOrThrow(email, TokenType.PASSWORD_RESET, req.token());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }
}
