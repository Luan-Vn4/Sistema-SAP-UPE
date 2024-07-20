package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoTerapeuticoRepository extends BasicRepository<GrupoTerapeutico, Integer> {

    GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico);

    GrupoTerapeutico addFuncionario(Integer uidFuncionario, Integer uidGrupoTerapeutico);

    GrupoTerapeutico addFuncionario(List<Integer> idsFuncionarios, Integer idGrupoTerapeutico);

    void addFicha(Integer uidFicha, Integer uidGrupoTerapeutico);
    void addFicha(List<Integer> idsFicha, Integer idGrupoTerapeutico);

    GrupoTerapeutico findById(Integer uidGrupoTerapeutico);

    List<GrupoTerapeutico> findByFuncionario(Integer uidFuncionario);
    
    GrupoTerapeutico findByFicha(Integer idFicha);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico);

    int removerFuncionario(Integer uidFUncionario, Integer uidGrupo);

    int removerFicha(Integer idFicha);

    int delete(Integer uidGrupoTerapeutico);

}
