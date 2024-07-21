package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import lombok.Builder;
import java.util.UUID;

@Builder
public record GrupoEstudoDTO (UUID uid, String tema, String descricao, int dono) {
    public static GrupoEstudoDTO from(GrupoEstudo grupoEstudo) {
        return new GrupoEstudoDTO(grupoEstudo.getUid(), grupoEstudo.getTema(), grupoEstudo.getDescricao(), grupoEstudo.getDono());
    }
    public static GrupoEstudo to(GrupoEstudoDTO grupoEstudoDTO) {
        return GrupoEstudo.grupoEstudoBuilder().tema(grupoEstudoDTO.tema())
                .dono(grupoEstudoDTO.dono()).descricao(grupoEstudoDTO.descricao()).build();
    }
}
