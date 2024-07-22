package br.upe.sap.sistemasapupe.api.dtos.atividades.geral;

import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AtividadeDTO {

    UUID idSala();

    UUID idFuncionario();

    LocalDateTime tempoInicio();

    LocalDateTime tempoFim();

    StatusAtividade statusAtividade();

}
