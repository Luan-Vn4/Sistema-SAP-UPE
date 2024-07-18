package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.Builder;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record  AtividadesDTO (UUID uid, Sala sala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                              StatusAtividade status, FuncionarioDTO funcionario){
    public static AtividadesDTO from (Atividade atividade){
        FuncionarioDTO funcionario = FuncionarioDTO.from(atividade.getFuncionario());
        return new AtividadesDTO(atividade.getUid(),atividade.getSala(),
                atividade.getTempoInicio(), atividade.getTempoFim(),
                atividade.getStatus(), funcionario);
    }
}
