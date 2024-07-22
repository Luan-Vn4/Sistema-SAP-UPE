package br.upe.sap.sistemasapupe.api.dtos.atividades.geral;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.EncontroDTO;

import java.util.List;

public record AnyAtividadeDTO (List<AtendimentoIndividualDTO> atendimentosIndividuais,
                               List<AtendimentoGrupoDTO> atendimentosGrupo,
                               List<EncontroDTO> encontros) {
}
