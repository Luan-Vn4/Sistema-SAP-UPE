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
    private LocalDateTime tempo_inicio;
    @Setter(AccessLevel.NONE)
    private LocalDateTime tempo_fim;
    private StatusAtividade status;


    public Atividade( int id, UUID uid, Sala sala, LocalDateTime tempo_inicio,
                     LocalDateTime tempo_fim, StatusAtividade status) {
        this.setId(id);
        this.setUid(uid);
        this.setSala(sala);
        this.setTempo(tempo_inicio, tempo_fim);
        this.setStatus(status);
    }

    private void setTempo(LocalDateTime tempo_inicio, LocalDateTime tempo_fim) {
        if (tempo_inicio.isAfter(tempo_fim) || tempo_inicio.equals(tempo_fim)) {
            throw new ScheduleException(tempo_inicio, tempo_fim);
        }
        this.tempo_inicio = tempo_inicio;
        this.tempo_fim = tempo_fim;
    }

}
