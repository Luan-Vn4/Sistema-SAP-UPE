package br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.BasicRepository;

import java.util.List;
import java.util.UUID;

public interface SalaRepository extends BasicRepository<Sala, Integer> {

    Sala create(Sala sala);

    List<Sala> create(List<Sala> salas);

    Sala update(Sala sala);

    List<Sala> update(List<Sala> sala);

    Sala findByUUID(UUID uuid);

    List<Sala> findByTipo(TipoSala tipoSala);

    Sala findByNome(String nome);

    Sala findById(Integer id);

    List<Sala> findAll();

    List<Sala> findByIds(List<Integer> ids);

    boolean exists(Integer id);

    int delete(Integer id);

    int delete(List<Integer> ids);

}
