package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;

import java.util.List;
import java.util.UUID;

public interface SalaRepository extends BasicRepository<Sala, Integer> {

    Sala create(Sala sala);

    public List<Sala> create(List<Sala> salas);

    public Sala update(Sala sala);

    public List<Sala> update(List<Sala> sala);

    public List<Sala> findByTipo(TipoSala tipoSala);

    public Sala findByNome(String nome);

    public Sala findById(Integer id);

    public List<Sala> findAll();

    public List<Sala> findById(List<Integer> ids);

    public boolean exists(Integer id);

    public int delete(Integer id);

    public int delete(List<Integer> ids);

}
