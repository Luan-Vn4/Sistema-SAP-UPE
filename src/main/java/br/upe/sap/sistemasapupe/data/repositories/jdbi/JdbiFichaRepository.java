package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class JdbiFichaRepository implements FichaRepository {
    Jdbi jdbi;

    JdbiFuncionariosRepository funcionariosRepository;

    private final static String returningColumns = "id, uid, nome, id_responsavel idResponsavel, " +
        "id_grupo_terapeutico idGrupoTerapeutico";

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
            RETURNING %s
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bindBean(ficha)
            .bind("nome", ficha.getNome())
            .bind("id_responsavel", ficha.getIdResponsavel())
            .bind("id_grupo_terapeutico", ficha.getIdGrupoTerapeutico())
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
            UPDATE fichas SET nome = :nome, id_responsavel =  :id_responsavel,
                id_grupo_terapeutico = :id_grupo_terapeutico WHERE id = :id
            RETURNING %s
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bind("nome", ficha.getNome())
            .bind("id_responsavel", ficha.getIdResponsavel())
            .bind("id_grupo_terapeutico", ficha.getIdGrupoTerapeutico())
            .bind("id", ficha.getId())
            .executeAndReturnGeneratedKeys()
            .mapToBean(Ficha.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Ficha> update(List<Ficha> fichas) {
        return fichas.stream().map(this::update).toList();
    }

    @Override
    public Ficha findById(Integer id) {
        final String QUERY = "SELECT %s FROM fichas WHERE id = :id".formatted(returningColumns);

        Optional<Ficha> resultado = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("id", id )
                .mapToBean(Ficha.class)
                .findFirst());
        return resultado.orElse(null);
    }

    @Override
    public List<Ficha> findAll() {
        final String QUERY = "SELECT %s FROM fichas".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .mapToBean(Ficha.class)
            .list());
    }

    @Override
    public List<Ficha> findById(List<Integer> ids) {
        final String QUERY = "SELECT * FROM fichas WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bindList("ids", ids)
                .mapToBean(Ficha.class)
                .list());
    }

    @Override
    public BidiMap<UUID, Integer> findIds(UUID uuid) {
        final String SELECT = "SELECT uid, id FROM fichas WHERE uid = :uuid LIMIT 1";

        BidiMap<UUID, Integer> results = new DualHashBidiMap<>();
        Map<String, Object> mapping = jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bind("uuid", uuid)
            .mapToMap()
            .findFirst().orElse(null));

        mapIds(results, mapping);

        return results;
    }

    private void mapIds(BidiMap<UUID, Integer> biMap, Map<String, Object> idsMap) {
        if (idsMap != null) {
            biMap.put((UUID) idsMap.get("uid"), (Integer) idsMap.get("id"));
        }
    }

    @Override
    public BidiMap<UUID, Integer> findIds(List<UUID> uuids) {
        final String SELECT = "SELECT uid, id FROM fichas WHERE uid IN (%s) LIMIT %d"
            .formatted("<uuids>", uuids.size());

        BidiMap<UUID, Integer> results = new DualHashBidiMap<>();
        List<Map<String, Object>> maps = jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bindList("uuids", uuids)
                .mapToMap()
                .collectIntoList());

        maps.forEach(x -> mapIds(results, x));

        return results;
    }

    @Override
    public boolean exists(Integer id) {
        final String QUERY = """
                SELECT COUNT(*) > 0 FROM fichas
                    WHERE id = :id GROUP BY id LIMIT 1
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .mapTo(Boolean.class)
            .findFirst().orElse(false));
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
        final String DELETE = "DELETE FROM fichas WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }

}
