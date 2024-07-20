package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GrupoEstudoDTO (UUID uid, String temaEstudo, String descricao, FuncionarioDTO dono, List<FuncionarioDTO> participantes) {
    public static GrupoEstudoDTO from(GrupoEstudo grupoEstudo) {
        return null;
    }
}
