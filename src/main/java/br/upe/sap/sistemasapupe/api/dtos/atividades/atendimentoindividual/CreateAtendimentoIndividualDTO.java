package br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CreateAtendimentoIndividualDTO(UUID idSala, LocalDateTime tempoInicio,
                                             LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                             UUID idTerapeuta, UUID idFicha, UUID idFuncionario){

    public AtendimentoIndividual to(Sala sala, Funcionario funcionario,Funcionario terapeuta, Ficha ficha) {
        return AtendimentoIndividual.builder()
            .tempoFim(tempoFim())
            .tempoInicio(tempoInicio())
            .statusAtividade(statusAtividade())
            .ficha(ficha)
            .funcionario(funcionario)
            .terapeuta(terapeuta)
            .sala(sala)
            .build();
    }

}
