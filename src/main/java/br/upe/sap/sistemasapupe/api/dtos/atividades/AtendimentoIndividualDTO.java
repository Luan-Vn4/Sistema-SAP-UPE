package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.ficha.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import lombok.Builder;

@Builder
public record AtendimentoIndividualDTO(FichaDTO ficha, FuncionarioDTO terapeuta){
    public static AtendimentoIndividualDTO from (AtendimentoIndividual atendimentoIndividual){
        FichaDTO ficha = FichaDTO.from(atendimentoIndividual.getFicha());
        FuncionarioDTO terapeuta = FuncionarioDTO.from(atendimentoIndividual.getTerapeuta());
        return new AtendimentoIndividualDTO(ficha, terapeuta);
    }
}
