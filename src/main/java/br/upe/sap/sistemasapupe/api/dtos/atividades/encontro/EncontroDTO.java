package br.upe.sap.sistemasapupe.api.dtos.atividades.encontro;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EncontroDTO (UUID id, UUID idSala, UUID idFuncionario, LocalDateTime tempoInicio,
                           LocalDateTime tempoFim, StatusAtividade statusAtividade,
                           UUID idGrupoEstudo) implements AtividadeDTO {

    public static EncontroDTO from(Encontro encontro, UUID uidGrupoEstudo) {
        return new EncontroDTO(
            encontro.getUid(),
            encontro.getSala().getUid(),
            encontro.getFuncionario().getUid(),
            encontro.getTempoInicio(),
            encontro.getTempoFim(),
            encontro.getStatus(),
            uidGrupoEstudo
        );
    }

}
