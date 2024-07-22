package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateEncontroDTO (UUID idSala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                                 StatusAtividade status, UUID idFuncionario, UUID idGrupoTerapeutico,
                                 List<UUID> idsPresentes) {

}
