package br.upe.sap.sistemasapupe.security.authentication.dtos.login;

import java.time.Instant;

public record TokenDTO (boolean valid, String token, String tokenType, String subject,
                        Instant expiration) {}
