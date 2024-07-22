package br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AtendimentoGrupoDTO (UUID id, UUID sala, LocalDateTime tempoInicio,
                                   LocalDateTime tempoFim, UUID funcionario, StatusAtividade statusAtividade,
                                   UUID idGrupoTerapeutico){

    public static AtendimentoGrupo to(AtendimentoGrupoDTO atendimentoGrupoDTO, Sala sala,
                                        Funcionario funcionario, Integer idGrupoTerapeutico){
        return AtendimentoGrupo.builder()
                .sala(sala)
                .funcionario(funcionario)
                .tempoInicio(atendimentoGrupoDTO.tempoInicio)
                .tempoFim(atendimentoGrupoDTO.tempoFim)
                .statusAtividade(atendimentoGrupoDTO.statusAtividade)
                .idGrupoTerapeutico(idGrupoTerapeutico)
                .build();
    }

    public static AtendimentoGrupoDTO from(AtendimentoGrupo atendimentoGrupo, UUID idGrupoTerapeutico){
        return new AtendimentoGrupoDTO(
                atendimentoGrupo.getUid(),
                atendimentoGrupo.getSala().getUid(),
                atendimentoGrupo.getTempoInicio(),
                atendimentoGrupo.getTempoFim(),
                atendimentoGrupo.getFuncionario().getUid(),
                atendimentoGrupo.getStatus(),
                idGrupoTerapeutico);
    }

}
