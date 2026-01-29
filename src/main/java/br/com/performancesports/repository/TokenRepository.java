package br.com.performancesports.repository;

import br.com.performancesports.domain.token.Token;
import br.com.performancesports.domain.token.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findFirstByEmailIgnoreCaseAndTokenHashAndType(String email, String tokenHash, TokenType type);

    List<Token> findAllByEmailIgnoreCaseAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(String email, TokenType type);
}

