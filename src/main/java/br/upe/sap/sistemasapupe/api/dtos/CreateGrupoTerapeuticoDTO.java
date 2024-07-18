package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

import java.util.List;

public record CreateGrupoTerapeuticoDTO(String temaTerapia, List<Funcionario> coordenadores,
                                        List<Ficha> fichas) {
    public static GrupoTerapeutico toGrupo(GrupoTerapeuticoDTO dto){
        return new GrupoTerapeutico(dto.temaTerapia(),
                dto.coordenadores(), dto.participantes());
    }
}
