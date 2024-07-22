package br.upe.sap.sistemasapupe.api.dtos.paciente;


import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FichaDTO (UUID id, UUID idResponsavel, String nome, UUID idGrupoTerapeutico){

    public static FichaDTO from (Ficha ficha, UUID idGrupoTerapeutico, UUID idResponsavel){
        return new FichaDTO(ficha.getUid(), idResponsavel,
                ficha.getNome(), idGrupoTerapeutico );
    }

    public static Ficha from (FichaDTO dto, int idGrupoTerapeutico, int idResponsavel){
        return Ficha.builder().uid(dto.id).idResponsavel(idResponsavel)
                .nome(dto.nome).idGrupoTerapeutico(idGrupoTerapeutico).build();
    }

}
