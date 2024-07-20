package br.upe.sap.sistemasapupe.api.dtos.atividades.sala;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;

import java.util.UUID;

public record CreateSalaDTO (String nome, TipoSala tipoSala) {
    public static Sala fromDTO(CreateSalaDTO dto) {
        return Sala.salaBuilder().nome(dto.nome()).tipoSala(dto.tipoSala).build();
    }
}
