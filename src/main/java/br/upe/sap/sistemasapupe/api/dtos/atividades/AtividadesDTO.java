package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.Builder;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record  AtividadesDTO (UUID uid, UUID idSala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                              StatusAtividade status, UUID idFuncionario){
    public static AtividadesDTO from(Atividade atividade){
        return new AtividadesDTO(atividade.getUid(), atividade.getSala().getUid(),
                atividade.getTempoInicio(), atividade.getTempoFim(), atividade.getStatus(),
                atividade.getFuncionario().getUid());
    }


}
