package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoTerapeuticoRepository extends BasicRepository<GrupoTerapeutico, Integer> {

    // CREATE
    GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico);

    GrupoTerapeutico addFuncionario(Integer idFuncionario, Integer idGrupoTerapeutico);

    GrupoTerapeutico addFuncionario(List<Integer> idsFuncionarios, Integer idGrupoTerapeutico);

    GrupoTerapeutico addFicha(Integer idFicha, Integer idGrupoTerapeutico);

    GrupoTerapeutico addFicha(List<Integer> idsFicha, Integer idGrupoTerapeutico);


    // READ //
    List<GrupoTerapeutico> findAll();

    GrupoTerapeutico findById(Integer uidGrupoTerapeutico);

    List<GrupoTerapeutico> findByFuncionario(Integer uidFuncionario);
    
    GrupoTerapeutico findByFicha(Integer idFicha);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    List<Funcionario> findCoordenadores(Integer idGrupoTerapeutico);

    List<Ficha> findFichas(Integer idGrupoTerapeutico);

    // UPDATE //
    GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico);

    List<Integer> findGruposTerapeuticosNaoParticipadosPor(Integer idParticipante);


    // DELETE //
    int removerFuncionario(Integer uidFUncionario, Integer uidGrupo);

    int removerFicha(Integer idFicha);

    int delete(Integer uidGrupoTerapeutico);

}
