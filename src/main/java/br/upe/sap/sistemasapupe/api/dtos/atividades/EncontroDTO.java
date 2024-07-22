package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record EncontroDTO (UUID id, UUID sala, LocalDateTime tempoInicio,
                           LocalDateTime tempoFim, StatusAtividade statusAtividade,
                           UUID funcionario, UUID idGrupoEstudo, List<UUID> idsPresentes) {}
