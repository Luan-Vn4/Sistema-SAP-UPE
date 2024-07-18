package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JdbiFichaRepository implements FichaRepository {

    Jdbi jdbi;
    JdbiFuncionariosRepository funcionariosRepository;

    public JdbiFichaRepository(Jdbi jdbi, JdbiFuncionariosRepository funcionariosRepository) {
        this.jdbi = jdbi;
        this.funcionariosRepository = funcionariosRepository;
    }

    @Override
    public Ficha findByFuncionario(Integer idFuncionario) {
        return null;
    }

    @Override
    public Ficha create(Ficha ficha) {
        return null;
    }

    @Override
    public List<Ficha> create(List<Ficha> fichas) {
        return List.of();
    }

    @Override
    public Ficha update(Ficha ficha) {
        return null;
    }

    @Override
    public List<Ficha> update(List<Ficha> fichas) {
        return List.of();
    }

    @Override
    public Ficha findById(Integer id) {
        return null;
    }

    @Override
    public List<Ficha> findAll() {
        return List.of();
    }

    @Override
    public List<Ficha> findById(List<Integer> ids) {
        return List.of();
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }

    @Override
    public int delete(List<Integer> integers) {
        return 0;
    }
}
