package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;

import lombok.Builder;

import java.util.List;

@Builder
public record AtendimentoGrupoDTO (GrupoTerapeuticoDTO grupoTerapeutico, List<FichaDTO> participantes,
                                   List<FuncionarioDTO> ministrantes){

}
