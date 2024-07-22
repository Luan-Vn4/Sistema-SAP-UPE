package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateEncontroDTO (UUID idSala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                                 StatusAtividade status, UUID idFuncionario, UUID idGrupoEstudo,
                                 List<UUID> idsPresentes) {
    public static Encontro from(CreateEncontroDTO dto, Sala sala,
                                Funcionario funcionario, Integer idGrupoEstudo, List<Integer> idsPresentes) {
        return Encontro.builder().tempoFim(dto.tempoFim).sala(sala)
                .tempoInicio(dto.tempoInicio).statusAtividade(dto.status())
                .funcionario(funcionario).idsPresentes(idsPresentes).idGrupoEstudo(idGrupoEstudo).build();
    }

}
