package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;

import java.util.List;
import java.util.UUID;

public interface SalaRepository extends BasicRepository<Sala, Integer> {

    Sala findByUUID(UUID uuid);

    List<Sala> findByTipo(TipoSala tipoSala);

    Sala findByNome(String nome);

    boolean exists(Integer id);

}
