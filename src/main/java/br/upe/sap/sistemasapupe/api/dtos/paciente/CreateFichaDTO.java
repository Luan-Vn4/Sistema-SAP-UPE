package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateFichaDTO (UUID idResponsavel, String nome) {

    public static Ficha fromDTO(CreateFichaDTO dto, int idResponsavel){
        return Ficha.builder().idResponsavel(idResponsavel).nome(dto.nome()).build();
    }
}
