package br.upe.sap.sistemasapupe.security.authentication.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO (@NotNull @Email String email, @NotNull String senha) {}
