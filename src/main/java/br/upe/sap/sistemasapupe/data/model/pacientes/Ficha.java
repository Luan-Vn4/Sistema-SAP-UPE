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
    private Funcionario responsavel;
    private String nome;
    private GrupoTerapeutico grupoTerapeutico;

    public Ficha(Funcionario funcionario, GrupoTerapeutico grupoTerapeutico, String nome){
        this.responsavel = funcionario;
        this.grupoTerapeutico = grupoTerapeutico;
        this.nome = nome;
    }

}
