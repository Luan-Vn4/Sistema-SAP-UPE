package br.upe.sap.sistemasapupe.api.dtos;

import java.util.UUID;

public record UpdateFuncionarioDTO(UUID uid, String nome, String email, String urlImagem,
                                   Boolean isAtivo) {
}
