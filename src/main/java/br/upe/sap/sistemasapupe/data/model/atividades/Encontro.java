package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ToString
@Getter @Setter
public class Encontro extends Atividade {

    private Integer idGrupoEstudo;

    private List<Integer> idsPresentes;

    public Encontro() {
        super();
    }

    @Builder
    public Encontro(int id, UUID uid, Sala sala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                    StatusAtividade statusAtividade, Integer idGrupoEstudo, List<Integer> idsPresentes,
                    Funcionario funcionario) {
        super(id, uid, sala, tempoInicio, tempoFim, statusAtividade, funcionario);
        this.idGrupoEstudo = idGrupoEstudo;
        this.idsPresentes = idsPresentes;
    }

}
