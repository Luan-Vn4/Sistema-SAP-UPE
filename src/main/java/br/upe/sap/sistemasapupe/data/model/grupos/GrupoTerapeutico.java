package br.upe.sap.sistemasapupe.data.model.grupos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GrupoTerapeutico {
    private int id;
    private UUID uid;
    private String tema;
    private String descricao;
    private int idDono;


    @Builder(builderMethodName = "grupoTerapeuticoBuilder")
    public GrupoTerapeutico(String tema, String descricao, int idDono) {
        this.tema = tema;
        this.descricao = descricao;
        this.idDono = idDono;
    }
}
