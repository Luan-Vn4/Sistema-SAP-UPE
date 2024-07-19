package br.upe.sap.sistemasapupe.data.model.grupos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
@NoArgsConstructor
public class GrupoTerapeutico {
    private int id;
    private UUID uid;
    private String temaTerapia;

    private List<Funcionario> coordenadores;
    private List<Ficha> fichas;

    @Builder(builderMethodName = "grupoTerapeuticoBuilder")
    public GrupoTerapeutico(String temaTerapia, List<Funcionario> coordenadores,
                            List<Ficha> fichas){
        this.temaTerapia = temaTerapia;
        this.coordenadores = coordenadores;
        this.fichas = fichas;
    }
}
