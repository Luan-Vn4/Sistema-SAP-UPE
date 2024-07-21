package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;

import java.util.List;
import java.util.UUID;

public record GrupoTerapeuticoDTO(UUID uid, String tema,
                                  String descricao, UUID idDono) {

    public static GrupoTerapeuticoDTO from (GrupoTerapeutico grupoTerapeutico,
                                     FuncionarioRepository repository){
        UUID uidDono = repository.findById(grupoTerapeutico.getIdDono()).getUid();

        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTema(),
                grupoTerapeutico.getDescricao(), uidDono);
    }
}
