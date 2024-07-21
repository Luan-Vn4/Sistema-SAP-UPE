package br.upe.sap.sistemasapupe.data.model.grupos;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoEstudo {
    private int id;
    private UUID uid;
<<<<<<< HEAD
    private String temaEstudo;
    private String descricao;
    private Funcionario dono;
=======
    private String tema;
    private String descricao;
    private Integer dono;

    @Builder(builderMethodName = "grupoEstudoBuilder")
    public GrupoEstudo(String tema, String descricao, int dono) {
        this.tema = tema;
        this.descricao = descricao;
        this.dono = dono;
    }
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
}
