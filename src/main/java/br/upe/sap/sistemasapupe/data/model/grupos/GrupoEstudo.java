package br.upe.sap.sistemasapupe.data.model.grupos;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoEstudo {
    private int id;
    private UUID uid;
    private String tema;
    private String descricao;
    private Integer dono;

    @Builder(builderMethodName = "grupoEstudoBuilder")
    public GrupoEstudo(String tema, String descricao, int dono) {
        this.tema = tema;
        this.descricao = descricao;
        this.dono = dono;
    }
}
