package br.upe.sap.sistemasapupe.api.dtos.paciente;


import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FichaDTO (UUID uid, int idResponsavel, String nome, UUID idGrupoTerapeutico){

    public static FichaDTO from (Ficha ficha, UUID idGrupoTerapeutico){
        return new FichaDTO(ficha.getUid(), ficha.getIdResponsavel(),
                ficha.getNome(), idGrupoTerapeutico );
    }

    public static Ficha from (FichaDTO dto, Integer idGrupoTerapeutico){
        return Ficha.builder().uid(dto.uid).idResponsavel(dto.idResponsavel)
                .nome(dto.nome).idGrupoTerapeutico(idGrupoTerapeutico).build();
    }

}
