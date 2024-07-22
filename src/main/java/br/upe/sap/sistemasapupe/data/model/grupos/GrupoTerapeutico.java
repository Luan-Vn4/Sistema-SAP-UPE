package br.upe.sap.sistemasapupe.data.model.grupos;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GrupoTerapeutico {
    private Integer id;
    private UUID uid;
    private String tema;
    private String descricao;
    private Integer idDono;


    @Builder(builderMethodName = "grupoTerapeuticoBuilder")
    public GrupoTerapeutico(String tema, String descricao, Integer idDono) {
        this.tema = tema;
        this.descricao = descricao;
        this.idDono = idDono;
    }
}
