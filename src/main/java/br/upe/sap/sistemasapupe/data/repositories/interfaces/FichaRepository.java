package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import org.apache.commons.collections4.BidiMap;

import java.util.List;
import java.util.UUID;

public interface FichaRepository extends BasicRepository<Ficha, Integer> {

    List<Ficha> findByFuncionario(Integer idFuncionario);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    boolean exists(Integer id);
}
