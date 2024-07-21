package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

@Builder
public record CreateFichaDTO (int idResponsavel, String nome, GrupoTerapeuticoDTO grupoTerapeutico) {

    //FALTA O DTO DO GRUPO TERAPEUTICOOOOOO
    public static Ficha fromDTO(CreateFichaDTO dto){
        return Ficha.fichaBuilder().idResponsavel(dto.idResponsavel())
                .nome(dto.nome()).build();
    }
}
