package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Sala {
    private int id;
    private UUID uid;
    private String nome;
    private TipoSala tipoSala;

    @Builder(builderMethodName = "salaBuilder")
    public Sala(int id, UUID uid, String nome, TipoSala tipoSala) {
        this.id = id;
        this.uid = uid;
        this.nome = nome;
        this.tipoSala = tipoSala;
    }
}
