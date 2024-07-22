package br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import java.time.LocalDateTime;
import java.util.UUID;

public record AtendimentoIndividualDTO(UUID id, UUID idSala, UUID idFuncionario, LocalDateTime tempoInicio,
                                       LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                       UUID idTerapeuta, UUID idFicha) implements AtividadeDTO {

    public static AtendimentoIndividual toAtendimentoIndividual(AtendimentoIndividualDTO atendimentoIndividualDTO, Sala sala,
                                             Funcionario funcionario, Funcionario terapeuta, Ficha ficha){
        return AtendimentoIndividual.builder()
            .sala(sala)
            .terapeuta(terapeuta)
            .funcionario(funcionario)
            .ficha(ficha)
            .tempoInicio(atendimentoIndividualDTO.tempoInicio)
            .tempoFim(atendimentoIndividualDTO.tempoFim)
            .statusAtividade(atendimentoIndividualDTO.statusAtividade)
            .build();
    }

    public static AtendimentoIndividualDTO from(AtendimentoIndividual atendimentoIndividual){
        return new AtendimentoIndividualDTO(
            atendimentoIndividual.getUid(),
            atendimentoIndividual.getSala().getUid(),
            atendimentoIndividual.getFuncionario().getUid(),
            atendimentoIndividual.getTempoInicio(),
            atendimentoIndividual.getTempoFim(),
            atendimentoIndividual.getStatus(),
            atendimentoIndividual.getTerapeuta().getUid(),
            atendimentoIndividual.getFicha().getUid()
        );
    }

}
