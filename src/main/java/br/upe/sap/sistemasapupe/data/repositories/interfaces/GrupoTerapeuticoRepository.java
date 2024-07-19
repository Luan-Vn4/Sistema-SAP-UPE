package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoTerapeuticoRepository extends BasicRepository<GrupoTerapeutico, Integer> {

    GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico);

    GrupoTerapeutico addFuncionario(Integer uidFuncionario, Integer uidGrupoTerapeutico);

    GrupoTerapeutico addFicha(Integer uidFicha, Integer uidGrupoTerapeutico);

    GrupoTerapeutico findById(Integer uidGrupoTerapeutico);

    List<GrupoTerapeutico> findByFuncionario(Integer uidFuncionario);
    
    List<GrupoTerapeutico> findByFicha(Integer idFicha);

    GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico);

    int removerFuncionario(Integer uidFUncionario, Integer uidGrupo);

    int removerFicha(Integer uidFicha, Integer uidGrupo);

    int delete(Integer uidGrupoTerapeutico);

}
