package br.com.performancesports.domain.exception;

import org.springframework.http.HttpStatus;

public class AccountStateException extends RuntimeException {

    private final HttpStatus status;

    public AccountStateException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AccountStateException emailNotVerified() {
        return new AccountStateException(HttpStatus.UNAUTHORIZED, "E-mail não verificado.");
    }

    public static AccountStateException pendingApproval() {
        return new AccountStateException(HttpStatus.FORBIDDEN, "Conta aguardando aprovação.");
    }

    public static AccountStateException suspended() {
        return new AccountStateException(HttpStatus.FORBIDDEN, "Conta suspensa.");
    }
}
