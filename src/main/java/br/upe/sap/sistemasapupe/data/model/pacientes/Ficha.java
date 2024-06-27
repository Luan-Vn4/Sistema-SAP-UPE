package br.upe.sap.sistemasapupe.data.model.pacientes;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Ficha {

    private int id;
    private UUID uid;
    private Funcionario responsavel;

}
