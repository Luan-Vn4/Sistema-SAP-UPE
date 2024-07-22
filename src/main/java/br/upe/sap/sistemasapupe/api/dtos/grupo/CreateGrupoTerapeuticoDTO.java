package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;

import java.util.UUID;

public record CreateGrupoTerapeuticoDTO(String tema, String descricao, UUID idDono) {

    public static GrupoTerapeutico toGrupo(CreateGrupoTerapeuticoDTO dto, Integer idDono){
        return GrupoTerapeutico.grupoTerapeuticoBuilder()
                .descricao(dto.descricao)
                .idDono(idDono)
                .tema(dto.tema)
                .build();
    }
}
