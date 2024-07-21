package br.upe.sap.sistemasapupe.data.model.pacientes;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Ficha {

    private int id;
    private UUID uid;
    private int idResponsavel;
    private String nome;
    private GrupoTerapeutico grupoTerapeutico;


    @Builder
    public Ficha(int id, UUID uid, int idResponsavel, String nome, GrupoTerapeutico grupoTerapeutico) {
        this.id = id;
        this.uid = uid;
        this.idResponsavel = idResponsavel;
        this.nome = nome;
        this.grupoTerapeutico = grupoTerapeutico;
    }

}
