package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoEstudoDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record EncontroDTO (GrupoEstudoDTO grupoEstudo, List<FuncionarioDTO> presentes) {}
