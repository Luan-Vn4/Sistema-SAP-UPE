package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
public class AtendimentoGrupo extends Atividade {
    private GrupoTerapeutico grupoTerapeutico;
    private List<Ficha> participantes;
    private List<Funcionario> ministrantes;

    public AtendimentoGrupo(int id, UUID uid, Sala sala, LocalDateTime tempoInicio,
                             LocalDateTime tempoFim, StatusAtividade statusAtividade,
                             GrupoTerapeutico grupoTerapeutico, List<Ficha> participantes,
                             List<Funcionario> ministrantes) {
        super(id, uid, sala, tempoInicio, tempoFim, statusAtividade);
        this.participantes = participantes;
        this.grupoTerapeutico = grupoTerapeutico;
        this.ministrantes = ministrantes;
    }

}
