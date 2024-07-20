package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
public class AtendimentoGrupo extends Atividade {

    private Integer idGrupoTerapeutico;

    private List<Integer> idsParticipantes;

    private List<Integer> idsMinistrantes;

    @Builder
    public AtendimentoGrupo(int id, UUID uid, Sala sala, LocalDateTime tempoInicio,
                            LocalDateTime tempoFim, StatusAtividade statusAtividade,
                            Funcionario funcionario, Integer idGrupoTerapeutico,
                            List<Integer> idsParticipantes, List<Integer> idsMinistrantes) {
        super(id, uid, sala, tempoInicio, tempoFim, statusAtividade, funcionario);
        this.idsParticipantes = idsParticipantes;
        this.idGrupoTerapeutico = idGrupoTerapeutico;
        this.idsMinistrantes = idsMinistrantes;
    }

}
