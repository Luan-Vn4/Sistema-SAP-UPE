package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CreateAtendimentoIndividualDTO(UUID sala, LocalDateTime tempoInicio,
                                             LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                             UUID terapeuta, UUID ficha, UUID funcionario){

    public static AtendimentoIndividual from(CreateAtendimentoIndividualDTO dto, Sala sala,
                                             Funcionario funcionario,Funcionario terapeuta, Ficha ficha) {
        return AtendimentoIndividual.builder().tempoFim(dto.tempoFim)
                .tempoInicio(dto.tempoInicio).statusAtividade(dto.statusAtividade)
                .ficha(ficha).funcionario(funcionario).terapeuta(terapeuta).sala(sala).build();
    }

}
