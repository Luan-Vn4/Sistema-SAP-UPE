package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.exceptions.ScheduleException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@ToString
@NoArgsConstructor
public class Atividade {

    private int id;
    private UUID uid;
    private Sala sala;
    @Setter(AccessLevel.NONE)
    private LocalDateTime tempoInicio;
    @Setter(AccessLevel.NONE)
    private LocalDateTime tempoFim;
    private StatusAtividade statusAtividade;


    public Atividade( int id, UUID uid, Sala sala, LocalDateTime tempoInicio,
                     LocalDateTime tempoFim, StatusAtividade statusAtividade) {
        this.setId(id);
        this.setUid(uid);
        this.setSala(sala);
        this.setTempo(tempoInicio, tempoFim);
        this.setStatusAtividade(statusAtividade);
    }

    private void setTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim) {
        if (tempoInicio.isAfter(tempoFim) || tempoInicio.equals(tempoFim)) {
            throw new ScheduleException(tempoInicio, tempoFim);
        }
        this.tempoInicio = tempoInicio;
        this.tempoFim = tempoFim;
    }

}
