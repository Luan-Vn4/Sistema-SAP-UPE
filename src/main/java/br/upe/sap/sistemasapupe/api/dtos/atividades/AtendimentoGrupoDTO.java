package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record AtendimentoGrupoDTO (UUID id, UUID sala, LocalDateTime tempoInicio,
                                   LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                   UUID idGrupoTerapeutico, List<UUID> idsParticipantes,
                                   List<UUID> idsMinistrantes){

}
