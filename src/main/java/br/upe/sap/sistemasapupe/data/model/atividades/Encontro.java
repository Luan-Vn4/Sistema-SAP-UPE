package br.upe.sap.sistemasapupe.data.model.atividades;


import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ToString
@Getter @Setter
public class Encontro extends Atividade {

    private GrupoEstudo grupoEstudo;
    private List<Funcionario> presentes;

    public Encontro(int id, UUID uid, Sala sala, LocalDateTime tempoInicio, LocalDateTime tempoFim,
                    StatusAtividade statusAtividade, GrupoEstudo grupoEstudo, List<Funcionario> presentes) {
        super(id, uid, sala, tempoInicio, tempoFim, statusAtividade);
        this.grupoEstudo = grupoEstudo;
        this.presentes = presentes;
    }

}
