package br.upe.sap.sistemasapupe.api.dtos.atividades.sala;


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
    public static Sala from(SalaDTO dto) {
        return Sala.salaBuilder().tipoSala(dto.tipoSala).uid(dto.uid).nome(dto.nome).build();
    }

}
