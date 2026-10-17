package com.bank.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String username,
            String tokenType
    ) {
        public static AuthResponse bearer(String token, String username) {
            return new AuthResponse(token, username, "Bearer");
        }
    }
}