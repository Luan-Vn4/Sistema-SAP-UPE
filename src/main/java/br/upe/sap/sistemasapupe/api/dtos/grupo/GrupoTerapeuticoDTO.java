package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema,
                                  String descricao, UUID idDono) {

    public static GrupoTerapeuticoDTO from (GrupoTerapeutico grupoTerapeutico, UUID idDono){

        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTema(),
                grupoTerapeutico.getDescricao(), idDono);
    }

    public static GrupoTerapeutico convertToGrupo(GrupoTerapeuticoDTO dto, Integer idDono){
        return new GrupoTerapeutico(dto.tema(), dto.descricao(), idDono);
    }
}
