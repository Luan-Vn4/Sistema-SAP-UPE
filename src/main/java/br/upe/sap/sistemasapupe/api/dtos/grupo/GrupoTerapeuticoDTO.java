package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

import java.util.List;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema, String descricao,
                                  List<UUID> coordenadores, List<UUID> fichas) {

    public static GrupoTerapeuticoDTO from(GrupoTerapeutico grupoTerapeutico){
        List<UUID> coordenadores = grupoTerapeutico.getCoordenadores().stream()
                .map(Funcionario::getUid).toList();
        List<UUID> fichas = grupoTerapeutico.getFichas().stream()
                .map(Ficha::getUid).toList();

        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTemaTerapia(),
                grupoTerapeutico.getDescricao(), coordenadores, fichas);
    }
}
