package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GrupoTerapeuticoDTO(UUID uid, String temaTerapia,
                                  List<Funcionario> coordenadores, List<Ficha> participantes) {
    public static GrupoTerapeuticoDTO from(GrupoTerapeutico grupoTerapeutico){
        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTemaTerapia(),
                grupoTerapeutico.getCoordenadores(), grupoTerapeutico.getFichas());
    }
}
