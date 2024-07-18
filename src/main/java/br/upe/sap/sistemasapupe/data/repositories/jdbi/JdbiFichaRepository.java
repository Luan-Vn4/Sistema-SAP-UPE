package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbiFichaRepository implements FichaRepository {

    Jdbi jdbi;
    JdbiFuncionariosRepository funcionariosRepository;

    @Override
    public List<Ficha> findByFuncionario(Integer idFuncionario) {
        if (idFuncionario == null) throw new IllegalArgumentException("UID do funcionário não deve ser nulo");

        final String SELECT = "SELECT id FROM fichas WHERE id_responsavel = :id_responsavel";

        List<Integer> ids = jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("id_responsavel", idFuncionario)
                .mapTo(Integer.class)
                .collectIntoList());

        return findById(ids);
    }

    @Override
    public Ficha create(Ficha ficha) {
        final String CREATE = """
            INSERT INTO fichas (nome, id_responsavel, id_grupo_terapeutico)
            VALUES (:nome, :id_responsavel, :id_grupo_terapeutico)
            RETURNING *
            """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bindBean(ficha)
                .bind("nome", ficha.getNome())
                .bind("id_responsavel", ficha.getResponsavel())
                .bind("id_grupo_terapeutico", ficha.getGrupoTerapeutico().getId())
                .executeAndReturnGeneratedKeys()
                .mapToBean(Ficha.class)
                .first());
    }

    @Override
    public List<Ficha> create(List<Ficha> fichas) {
        return fichas.stream().map(this::create).toList();
    }

    @Override
    public Ficha update(Ficha ficha) {
        final String UPDATE = """
                UPDATE fichas
                SET nome = :nome,
                id_responsavel =  :id_responsavel,
                id_grupo_terapeutico = :id_grupo_terapeutico
                WHERE id = :id
                """;
        jdbi.useHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("nome", ficha.getNome())
                .bind("id_responsavel", ficha.getResponsavel().getId())
                .bind("id_grupo_terapeutico", ficha.getGrupoTerapeutico().getId())
                .execute()
        );

        final String SELECT = """
            SELECT *
            FROM fichas
            WHERE id = :id
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("id", ficha.getId())
                .mapToBean(Ficha.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public List<Ficha> update(List<Ficha> fichas) {
        throw new UnsupportedOperationException("Atualizações em lote não são suportadas.");
    }

    @Override
    public Ficha findById(Integer id) {

        final String QUERY = """
                SELECT *
                FROM fichas
                WHERE id = :id
                """;
        Optional<Ficha> resultado = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("id", id )
                .mapToBean(Ficha.class)
                .findFirst());
        return resultado.orElse(null);
    }

    @Override
    public List<Ficha> findAll() {

        final String QUERY = """
                SELECT *
                FROM fichas
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .mapToBean(Ficha.class)
                .list());
    }

    @Override
    public List<Ficha> findById(List<Integer> ids) {
        final String QUERY = """
                SELECT *
                FROM fichas
                WHERE ids IN (%s)
                """.formatted("<ids>");
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bindList("ids", ids)
                .mapToBean(Ficha.class)
                .list());
    }

    @Override
    public int delete(Integer id) {
        final String DELETE = """
                DELETE FROM fichas
                WHERE id = :id
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bind("id", id)
                .execute());
    }

    @Override
    public int delete(List<Integer> ids) {

        final String DELETE = """
                DELETE FROM fichas
                WHERE id IN (%s)""".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }
}
