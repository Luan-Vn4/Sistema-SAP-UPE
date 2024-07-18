package br.upe.sap.sistemasapupe.api.dtos.funcionarios;

import java.util.UUID;

public record UpdateFuncionarioDTO(UUID id, String nome, String email, String urlImagem,
                                   Boolean isAtivo) {
}
