package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

@Builder
public record CreateFichaDTO (int idResponsavel, String nome) {

    public static Ficha fromDTO(CreateFichaDTO dto){
        return Ficha.builder().idResponsavel(dto.idResponsavel())
                .nome(dto.nome()).build();
    }
}
