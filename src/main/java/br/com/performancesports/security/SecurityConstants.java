package br.com.performancesports.security;

public final class SecurityConstants {

    private SecurityConstants() {}

    // Ajuste quando criar o endpoint real de login
    public static final String LOGIN_ENDPOINT = "/auth/login";

    // Endpoints liberados sem token (você pode ampliar depois)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/actuator/health",
            "/actuator/info",
            "/auth/**"
    };
}
