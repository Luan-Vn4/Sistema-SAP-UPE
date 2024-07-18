package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.exceptions.ScheduleException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@ToString
@NoArgsConstructor
public abstract class Atividade {

    private int id;

    private UUID uid;

    private Sala sala;

    private Funcionario funcionario;

    private LocalDateTime tempoInicio;

    private LocalDateTime tempoFim;

    private StatusAtividade status;

    public Atividade(int id, UUID uid, Sala sala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                     StatusAtividade status, Funcionario funcionario) {
        this.setId(id);
        this.setUid(uid);
        this.setSala(sala);
        this.setTempo(tempoInicio, tempoFim);
        this.setStatus(status);
        this.setFuncionario(funcionario);
    }

    private void setTempo(LocalDateTime tempo_inicio, LocalDateTime tempo_fim) {
        if (tempo_inicio.isAfter(tempo_fim) || tempo_inicio.equals(tempo_fim)) {
            throw new ScheduleException(tempo_inicio, tempo_fim);
        }
        this.tempoInicio = tempo_inicio;
        this.tempoFim = tempo_fim;
    }

}
