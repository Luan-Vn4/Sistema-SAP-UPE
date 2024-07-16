package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {
    JdbiGrupoTerapeuticoRepository grupoTerapeuticoRepository;
}
