package br.com.performancesports.service;

import br.com.performancesports.domain.token.Token;
import br.com.performancesports.domain.token.TokenType;
import br.com.performancesports.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    private static final int MAX_ATTEMPTS = 5;
    private static final int EXPIRES_SECONDS = 60;
    private static final int COOLDOWN_MINUTES = 10;

    public record GeneratedToken(String rawToken, LocalDateTime expiresAt) {}

    public GeneratedToken generate(String email, TokenType type) {
        String raw = UUID.randomUUID().toString().replace("-", "");
        String hash = sha256Base64(raw);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(EXPIRES_SECONDS);

        Token token = Token.builder()
                .email(email)
                .type(type)
                .tokenHash(hash)
                .expiresAt(expiresAt)
                .attempts(0)
                .cooldownUntil(null)
                .consumedAt(null)
                .build();

        tokenRepository.save(token);

        return new GeneratedToken(raw, expiresAt);
    }

    public void assertValidOrThrow(String email, TokenType type, String rawToken) {
        LocalDateTime now = LocalDateTime.now();

        var tokens = tokenRepository.findAllByEmailIgnoreCaseAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(email, type);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        Token token = tokens.get(0);

        if (token.inCooldown(now)) {
            throw new IllegalArgumentException("Aguarde alguns minutos e tente novamente.");
        }

        if (token.isConsumed() || token.isExpired(now)) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        String expectedHash = sha256Base64(rawToken);

        // token incorreto: registra tentativa
        if (!expectedHash.equals(token.getTokenHash())) {
            registerInvalidAttempt(token, now);
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        // válido: consome
        token.setConsumedAt(now);
        tokenRepository.save(token);
    }

    public void assertValidOnly(String email, TokenType type, String rawToken) {
        LocalDateTime now = LocalDateTime.now();

        var tokens = tokenRepository.findAllByEmailIgnoreCaseAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(email, type);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        Token token = tokens.get(0);

        if (token.inCooldown(now)) {
            throw new IllegalArgumentException("Aguarde alguns minutos e tente novamente.");
        }

        if (token.isConsumed() || token.isExpired(now)) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        String expectedHash = sha256Base64(rawToken);

        if (!expectedHash.equals(token.getTokenHash())) {
            registerInvalidAttempt(token, now);
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        // IMPORTANTE: aqui NÃO consome (não seta consumedAt)
    }



    private void registerInvalidAttempt(Token token, LocalDateTime now) {
        int attempts = token.getAttempts() + 1;
        token.setAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            token.setCooldownUntil(now.plusMinutes(COOLDOWN_MINUTES));
        }

        tokenRepository.save(token);
    }

    private String sha256Base64(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar hash do token.", e);
        }
    }
}
