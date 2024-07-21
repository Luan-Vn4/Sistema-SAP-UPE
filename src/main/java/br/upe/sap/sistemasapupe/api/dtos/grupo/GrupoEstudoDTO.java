package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record GrupoEstudoDTO (UUID uid, String temaEstudo, List<FuncionarioDTO> participantes) {

}
