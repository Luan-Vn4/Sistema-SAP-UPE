package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;

import java.util.UUID;

public record CreateGrupoEstudoDTO(String tema, String descricao, UUID dono) {

    public static GrupoEstudo to(CreateGrupoEstudoDTO createGrupoEstudoDTO, Integer dono) {
        return GrupoEstudo.grupoEstudoBuilder().descricao(createGrupoEstudoDTO.descricao)
                .dono(dono).tema(createGrupoEstudoDTO.tema()).build();
    }
}
