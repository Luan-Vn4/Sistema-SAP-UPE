package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import java.util.UUID;

public record UpdateFichaDTO (UUID uid, UUID idResponsavel, String nome, UUID idGrupoTerapeutico) {

    public static Ficha from(UpdateFichaDTO dto, int idFicha, int idResponsavel, int idGrupoTerapeutico) {
        return Ficha.builder()
        .id(idFicha)
        .uid(dto.uid())
        .idResponsavel(idResponsavel)
        .idGrupoTerapeutico(idGrupoTerapeutico)
        .nome(dto.nome()).build();
    }

}
