package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import org.apache.commons.collections4.BidiMap;

import java.util.List;
import java.util.UUID;

public interface GrupoEstudoRepository extends BasicRepository<GrupoEstudo, Integer> {

    List<GrupoEstudo> findByFuncionario(Integer idFuncionario);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    Funcionario addFuncionario(Integer idFuncionario, Integer idGrupoEstudo);

    int deleteParticipacao(int idParticipante, int idGrupoEstudo);

    List<Integer> findParticipantesByGrupoEstudo(Integer idGrupoEstudo);
}
