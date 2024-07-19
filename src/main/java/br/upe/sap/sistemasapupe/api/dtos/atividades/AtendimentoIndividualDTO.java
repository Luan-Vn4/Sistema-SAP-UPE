package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import lombok.Builder;

@Builder
public record AtendimentoIndividualDTO(FichaDTO ficha, FuncionarioDTO terapeuta){

}
