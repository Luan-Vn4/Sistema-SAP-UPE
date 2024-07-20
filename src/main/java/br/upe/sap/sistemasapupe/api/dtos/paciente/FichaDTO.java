package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FichaDTO (UUID uid, int idResponsavel, String nome, GrupoTerapeuticoDTO grupoTerapeutico){

    //FALTA O DTO DO GRUPO TERAPEUTICOOOOOO
    public static FichaDTO from (Ficha ficha){
        return new FichaDTO(ficha.getUid(), ficha.getIdResponsavel(),
                ficha.getNome(), );
    }

    //FALTA O DTO DO GRUPO TERAPEUTICOOOOOOOO
    public static Ficha from (FichaDTO dto){
        return Ficha.fichaBuilder().uid(dto.uid).idResponsavel(dto.idResponsavel)
                .nome(dto.nome).build();
    }

}
