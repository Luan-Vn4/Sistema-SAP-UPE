package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.SalaRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class JdbiSalaRepository implements SalaRepository {

    private Jdbi jdbi;

    private final String returningColumns = "id, uid, nome, tipo";

    // CREATE

    @Override
    public Sala create(Sala sala) {
        final String CREATE = "INSERT INTO salas (nome, tipo) VALUES (:nome, :tipo) RETURNING %s"
            .formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bindBean(sala)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Sala.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Sala> create(List<Sala> salas) {
        return salas.stream().map(this::create).toList();
    }

    // UPDATE
    @Override
    public Sala update(Sala sala) {
        final String UPDATE = "UPDATE salas SET nome = :nome, tipo = :tipo WHERE id = :id RETURNING %s"
            .formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bindBean(sala)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Sala.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Sala> update(List<Sala> salas) {
        return salas.stream().map(this::update).toList();
    }

    // READ
    @Override
    public Sala findByUUID(UUID uuid) {
        final String SELECT = "SELECT %s FROM salas WHERE uid = :uuid".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .mapToBean(Sala.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Sala> findByTipo(TipoSala tipoSala) {
        final String SELECT = "SELECT %s FROM salas WHERE tipo = :tipo".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .mapToBean(Sala.class)
            .collectIntoList());
    }

    @Override
    public Sala findByNome(String nome) {
        final String SELECT = "SELECT %s FROM salas WHERE nome = :nome".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .mapToBean(Sala.class)
            .findFirst().orElse(null));
    }

    @Override
    public Sala findById(Integer id) {
        final String SELECT = "SELECT %s FROM salas WHERE id = :id".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .mapToBean(Sala.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Sala> findAll() {
        final String SELECT = "SELECT %s FROM salas".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .mapToBean(Sala.class)
            .collectIntoList());
    }

    @Override
    public List<Sala> findById(List<Integer> ids) {
        final String SELECT = "SELECT %s FROM salas WHERE id IN (%s)"
            .formatted(returningColumns, "<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bindList("ids", ids)
            .mapToBean(Sala.class)
            .collectIntoList());
    }

    @Override
    public boolean exists(Integer id) {
        final String SELECT = "SELECT COUNT(*) count FROM salas WHERE id = :id GROUP BY id";

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bind("id", id)
            .map((rs, ctx) -> (rs.getInt("count") != 0))
            .findFirst().orElse(false));
    }

    // DELETE
    @Override
    public int delete(Integer id) {
        final String DELETE = "DELETE FROM salas WHERE id = :id";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("id", id)
            .execute());
    }

    @Override
    public int delete(List<Integer> ids) {
        final String DELETE = "DELETE FROM salas WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("ids", ids)
            .execute());
    }

}
