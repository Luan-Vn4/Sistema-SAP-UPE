package br.upe.sap.sistemasapupe.data.model.pacientes;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
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

    @Builder(builderMethodName = "fichaBuilder")
    public Ficha(int funcionario, GrupoTerapeutico grupoTerapeutico, String nome){
        this.idResponsavel = funcionario;
        this.grupoTerapeutico = grupoTerapeutico;
        this.nome = nome;
    }

}
