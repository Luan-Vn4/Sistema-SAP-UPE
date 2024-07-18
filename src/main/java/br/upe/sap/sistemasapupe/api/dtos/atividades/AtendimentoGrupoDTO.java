package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;

import lombok.Builder;

import java.util.List;

@Builder
public record AtendimentoGrupoDTO (GrupoTerapeuticoDTO grupoTerapeutico, List<FichaDTO> participantes,
                                   List<FuncionarioDTO> ministrantes){

    public static AtendimentoGrupoDTO from (AtendimentoGrupo atendimentoGrupo){

        GrupoTerapeuticoDTO grupoTerapeutico = GrupoTerapeuticoDTO.from(atendimentoGrupo.getGrupoTerapeutico());

        List<FuncionarioDTO> ministrantes = atendimentoGrupo.getMinistrantes().stream()
            .map(FuncionarioDTO::from).toList();

        List<FichaDTO> participantes = atendimentoGrupo.getParticipantes().stream()
                .map(FichaDTO::from).toList();

        return new AtendimentoGrupoDTO(grupoTerapeutico,
                participantes, ministrantes);
    }
}
