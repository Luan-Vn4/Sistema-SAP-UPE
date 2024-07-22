package br.upe.sap.sistemasapupe.api.dtos.atividades.encontro;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEncontroDTO(UUID idSala, UUID idFuncionario, LocalDateTime tempoInicio,
                                LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                UUID idGrupoEstudo) implements AtividadeDTO {

    public Encontro toEncontro(Sala sala, Funcionario funcionario, int idGrupoEstudo) {
        return Encontro.builder()
            .sala(sala)
            .funcionario(funcionario)
            .tempoInicio(tempoInicio())
            .tempoFim(tempoFim())
            .statusAtividade(statusAtividade())
            .idGrupoEstudo(idGrupoEstudo)
            .build();
    }

}
