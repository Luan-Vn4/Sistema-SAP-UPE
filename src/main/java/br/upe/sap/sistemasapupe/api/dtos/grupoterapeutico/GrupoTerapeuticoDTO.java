package br.upe.sap.sistemasapupe.api.dtos.grupoterapeutico;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

import java.util.List;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema,
                                  List<Funcionario> coordenador, List<Ficha> fichas) {

    public static GrupoTerapeuticoDTO from(GrupoTerapeutico grupoTerapeutico){
        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(),
                grupoTerapeutico.getTemaTerapia(), grupoTerapeutico.getCoordenadores(),
                grupoTerapeutico.getFichas());
    }
}
