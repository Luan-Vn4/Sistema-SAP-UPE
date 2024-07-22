package br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAtendimentoGrupoDTO(UUID idSala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                                        StatusAtividade status, UUID idFuncionario, UUID idGrupoTerapeutico) {

    public static AtendimentoGrupo to(CreateAtendimentoGrupoDTO dto,
                                      Integer idGrupoTerapeutico, Sala sala, Funcionario funcionario) {
        return AtendimentoGrupo.builder().idGrupoTerapeutico(idGrupoTerapeutico)
                    .sala(sala)
                    .funcionario(funcionario)
                    .tempoInicio(dto.tempoInicio())
                    .tempoFim(dto.tempoFim())
                    .statusAtividade(dto.status()).build();
    }
}
