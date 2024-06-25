package br.upe.sap.sistemasapupe.data.model.grupos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@ToString
public class GrupoTerapeutico {
    private int id;
    private UUID uid;
    private String temaTerapia;
    private List<Funcionario> coordenadores;
    private List<Ficha> fichas;
}
