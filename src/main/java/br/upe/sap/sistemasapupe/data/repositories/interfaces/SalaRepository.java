package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaRepository extends BasicRepository<Sala, Integer> {

    Sala create(Sala sala);

    List<Sala> create(List<Sala> salas);

    Sala update(Sala sala);

    List<Sala> update(List<Sala> sala);

    List<Sala> findByTipo(TipoSala tipoSala);

    Sala findByNome(String nome);

    Sala findById(Integer id);

    List<Sala> findAll();

    List<Sala> findById(List<Integer> ids);

    boolean exists(Integer id);

    int delete(Integer id);

    int delete(List<Integer> ids);

    BidiMap<UUID, Integer> findIds(List<UUID> uids);

    BidiMap<UUID, Integer> findIds(UUID uid);
}
