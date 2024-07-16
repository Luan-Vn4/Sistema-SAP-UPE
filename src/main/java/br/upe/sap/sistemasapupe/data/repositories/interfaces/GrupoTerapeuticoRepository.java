package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoTerapeuticoRepository extends BasicRepository<GrupoTerapeutico, UUID> {

    GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico);

    GrupoTerapeutico addFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico);

    GrupoTerapeutico addFicha(UUID uidFicha, UUID uidGrupoTerapeutico);

    GrupoTerapeutico findById(UUID uidGrupoTerapeutico);

    List<GrupoTerapeutico> findByFuncionario(UUID uidFuncionario);
    
    List<GrupoTerapeutico> findByFicha(UUID idFicha);

    GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico);

    int delete(UUID uidGrupoTerapeutico);

}
