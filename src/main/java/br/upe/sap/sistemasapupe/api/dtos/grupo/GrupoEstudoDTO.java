package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import lombok.Builder;
import java.util.UUID;

@Builder
public record GrupoEstudoDTO (UUID id, String tema, String descricao, UUID dono) {
    public static GrupoEstudoDTO from(GrupoEstudo grupoEstudo, UUID dono) {
        return new GrupoEstudoDTO(grupoEstudo.getUid(), grupoEstudo.getTema(), grupoEstudo.getDescricao(), dono);
    }

    public static GrupoEstudo to(GrupoEstudoDTO grupoEstudoDTO, Integer dono) {
        return GrupoEstudo.grupoEstudoBuilder().tema(grupoEstudoDTO.tema())
                .dono(dono).descricao(grupoEstudoDTO.descricao()).build();
    }
}
