package br.upe.sap.sistemasapupe.data.model.atividades;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter @Setter
public class AtendimentoIndividual extends Atividade {

    private Ficha ficha;

    private Funcionario terapeuta;

    @Builder
    public AtendimentoIndividual(int id, UUID uid, Sala sala, LocalDateTime tempoInicio,
                                 LocalDateTime tempoFim, StatusAtividade statusAtividade,
                                 Ficha ficha, Funcionario terapeuta) {
        super(id, uid, sala, tempoInicio, tempoFim, statusAtividade, terapeuta);
        this.ficha = ficha;
        this.terapeuta = terapeuta;
    }

}
