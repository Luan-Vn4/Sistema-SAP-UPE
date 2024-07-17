package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {
    JdbiGrupoTerapeuticoRepository repository;

    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico){
        return repository.create(grupoTerapeutico);
    }


}
