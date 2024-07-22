package br.upe.sap.sistemasapupe.data.model.pacientes;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Ficha {

    private Integer id;
    private UUID uid;
    private int idResponsavel;
    private String nome;
    private Integer idGrupoTerapeutico;


    @Builder
    public Ficha(Integer id, UUID uid, int idResponsavel, String nome, Integer idGrupoTerapeutico) {
        this.id = id;
        this.uid = uid;
        this.idResponsavel = idResponsavel;
        this.nome = nome;
        this.idGrupoTerapeutico = idGrupoTerapeutico;
    }

}
