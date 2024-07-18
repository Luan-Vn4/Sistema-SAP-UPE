package br.upe.sap.sistemasapupe.api.dtos.atividades;


import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SalaDTO (UUID uid, String nome, TipoSala tipoSala){
    public static SalaDTO from(Sala sala) {
        return new SalaDTO(sala.getUid(), sala.getNome(),
                sala.getTipoSala());
    }

}
