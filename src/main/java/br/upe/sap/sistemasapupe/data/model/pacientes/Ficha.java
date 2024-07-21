package br.upe.sap.sistemasapupe.data.model.pacientes;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Ficha {

    private int id;
    private UUID uid;
    private int idResponsavel;
    private String nome;
    private GrupoTerapeutico grupoTerapeutico;

<<<<<<< HEAD
    @Builder(builderMethodName = "fichaBuilder")
    public Ficha(int id, UUID uid, int idResponsavel, String nome, GrupoTerapeutico grupoTerapeutico){
        this.id = id;
        this.uid = uid;
        this.idResponsavel = idResponsavel;
=======
    @Builder
    public Ficha(int id, UUID uid, int idFuncionario, GrupoTerapeutico grupoTerapeutico, String nome){
        this.idResponsavel = idFuncionario;
        this.grupoTerapeutico = grupoTerapeutico;
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
        this.nome = nome;
        this.grupoTerapeutico = grupoTerapeutico;

    }

}
