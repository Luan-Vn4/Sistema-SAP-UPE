package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;

import java.util.List;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema,
                                  List<FuncionarioDTO> coordenador, List<FichaDTO> fichas) {

    public static GrupoTerapeuticoDTO from(GrupoTerapeutico grupoTerapeutico){
        List<FuncionarioDTO> coordenador = grupoTerapeutico.getCoordenadores().stream()
                .map(FuncionarioDTO::from).toList();
        List<FichaDTO> fichas = grupoTerapeutico.getFichas().stream()
                .map(FichaDTO::from).toList();

        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTemaTerapia(),
                coordenador, fichas);
    }
}
