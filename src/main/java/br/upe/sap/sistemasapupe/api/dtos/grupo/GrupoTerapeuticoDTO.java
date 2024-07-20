package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;

import java.util.List;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema,
                                  List<FuncionarioDTO> coordenador, List<FichaDTO> fichas) {

}
