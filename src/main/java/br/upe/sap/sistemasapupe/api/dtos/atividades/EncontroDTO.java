package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoEstudoDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import lombok.Builder;

import java.util.List;

@Builder
public record EncontroDTO (GrupoEstudoDTO grupoEstudo, List<FuncionarioDTO> presentes){
    public static EncontroDTO from (Encontro encontroEstudo){
        GrupoEstudoDTO grupoEstudo = GrupoEstudoDTO.from(encontroEstudo.getGrupoEstudo());
        List<FuncionarioDTO> presentes = encontroEstudo.getPresentes().stream()
                .map(FuncionarioDTO::from).toList();
        return new EncontroDTO(grupoEstudo,presentes);
    }
}
