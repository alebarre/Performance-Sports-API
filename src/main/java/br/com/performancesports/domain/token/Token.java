package br.com.performancesports.domain.token;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens",
        indexes = {
                @Index(name = "idx_tokens_email_type", columnList = "email,type")
        })
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TokenType type;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "cooldown_until")
    private LocalDateTime cooldownUntil;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt) || now.isEqual(expiresAt);
    }

    public boolean inCooldown(LocalDateTime now) {
        return cooldownUntil != null && now.isBefore(cooldownUntil);
    }
}
