package br.upe.sap.sistemasapupe.data.model.grupos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class GrupoEstudo {
    private int id;
    private UUID uid;
    private String temaEstudo;
    private List<Integer> idsParticipantes;
    private Funcionario dono;
}
