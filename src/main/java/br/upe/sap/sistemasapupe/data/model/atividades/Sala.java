package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Sala {
    private int id;
    private TipoSala tipoSala;
}
