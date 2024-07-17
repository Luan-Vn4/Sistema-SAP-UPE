package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFuncionariosRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {
    JdbiGrupoTerapeuticoRepository grupoTerapeuticoRepository;
    JdbiFuncionariosRepository funcionariosRepository;


    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico){
        return grupoTerapeuticoRepository.create(grupoTerapeutico);
    }

    // Preciso do
    public GrupoTerapeutico convertToDTO(GrupoTerapeutico grupoTerapeutico){

    }
}
