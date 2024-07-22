package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JdbiGrupoTerapeuticoRepository implements GrupoTerapeuticoRepository {
    Jdbi jdbi;

    public JdbiGrupoTerapeuticoRepository(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    private String createGrupoTerapeuticoSQL(){
        return """
            INSERT INTO grupos_terapeuticos(id_dono, tema, descricao) VALUES
                (:id_dono, :tema, :descricao)
                RETURNING id, uid, id_dono idDono, tema, descricao;
        """;
    }

    @Override
    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico) {
        final String CREATE = createGrupoTerapeuticoSQL();

        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_dono", grupoTerapeutico.getIdDono())
                .bind("tema", grupoTerapeutico.getTema())
                .bind("descricao", grupoTerapeutico.getDescricao())
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .first());
    }

    @Override
    public List<GrupoTerapeutico> create(List<GrupoTerapeutico> grupoTerapeuticos) {
        return grupoTerapeuticos.stream().map(this::create).toList();
    }

    @Override
    public GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico) {
        final String query = """
                UPDATE grupos_terapeuticos
                SET tema = :tema, descricao = :descricao, id_dono = :id_dono
                WHERE id = :id
                RETURNING id, uid, id_dono idDono, tema, descricao;
            """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", grupoTerapeutico.getId())
                .bind("id_dono", grupoTerapeutico.getIdDono())
                .bind("tema", grupoTerapeutico.getTema())
                .bind("descricao", grupoTerapeutico.getDescricao())
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .first());
    }

    @Override
    public List<GrupoTerapeutico> update(List<GrupoTerapeutico> grupoTerapeuticos) {
        return jdbi.inTransaction(handle -> {
            List<GrupoTerapeutico> result = new ArrayList<>();
            for (GrupoTerapeutico grupoTerapeutico : grupoTerapeuticos){ result.add(update(grupoTerapeutico));
            }
            return result;
        });
    }

    // procurar o grupo pelo id dele mesmo
    @Override
    public GrupoTerapeutico findById(Integer id) {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                WHERE id = :id
                """;

        Optional<GrupoTerapeutico> result = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("id", id)
                .mapToBean(GrupoTerapeutico.class)
                .findFirst());

        return result.orElse(null);
    }

    @Override
    public List<GrupoTerapeutico> findById(List<Integer> ids) {
        final String QUERY = """
            SELECT * FROM grupos_terapeuticos
            WHERE id IN (<ids>)
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids",ids)
            .mapToBean(GrupoTerapeutico.class)
            .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findAll() {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findByFuncionario(Integer idFuncionario) {
        final String query = """
            SELECT g.*
            FROM grupos_terapeuticos g
            JOIN participacao_grupo_terapeutico p ON g.id = p.id_grupo_terapeutico
            WHERE p.id_funcionario = :idFuncionario
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("idFuncionario", idFuncionario)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public GrupoTerapeutico findByFicha(Integer idFicha) {
        final String query = """
            SELECT id_grupo_terapeutico
            FROM fichas
            WHERE id = :idFicha
            """;

        Integer idGrupoTerapeutico = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("idFicha", idFicha)
                .mapTo(Integer.class)
                .findFirst()
                .orElse(null));

        if (idGrupoTerapeutico == null) {
            return null;
        }

        return findById(idGrupoTerapeutico);
    }

    @Override
    public BidiMap<UUID, Integer> findIds(UUID uuid) {
        final String SELECT = "SELECT uid, id FROM grupos_terapeuticos WHERE uid = :uuid LIMIT 1";

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
        final String SELECT = "SELECT uid, id FROM grupos_terapeuticos WHERE uid IN (%s) LIMIT %d"
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
    public GrupoTerapeutico addFuncionario(Integer idFuncionario, Integer idGrupoTerapeutico) {
        final String query = """
            WITH id_func AS (
            SELECT id FROM funcionarios WHERE id = :id_funcionario LIMIT 1),
            id_grupo AS (
            SELECT id FROM grupos_terapeuticos WHERE id = :id_grupo LIMIT 1)
            INSERT INTO participacao_grupo_terapeutico(id_funcionario, id_grupo_terapeutico)
            VALUES ((SELECT id FROM id_func), (SELECT id FROM id_grupo))
            """;

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_funcionario", idFuncionario)
                .bind("id_grupo", idGrupoTerapeutico)
                .execute());

        return findById(idGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFuncionario(List<Integer> idsFuncionarios, Integer idGrupoTerapeutico) {
        final String query = """
            INSERT INTO participacao_grupo_terapeutico(id_funcionario, id_grupo_terapeutico)
            VALUES (:id_funcionario, :id_grupo)
            ON CONFLICT (id_funcionario, id_grupo_terapeutico) DO NOTHING
            """;

        jdbi.useTransaction(handle -> {
            for (Integer idFuncionario : idsFuncionarios) {
                handle.createUpdate(query)
                        .bind("id_funcionario", idFuncionario)
                        .bind("id_grupo", idGrupoTerapeutico)
                        .execute();
            }
        });

        return findById(idGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFicha(Integer idFicha, Integer idGrupoTerapeutico) {
        final String query = """
            UPDATE fichas
            SET id_grupo_terapeutico = :id_grupo_terapeutico
            WHERE id = :idFicha
            """;

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_grupo_terapeutico", idGrupoTerapeutico)
                .bind("idFicha", idFicha)
                .execute());

        return findById(idGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFicha(List<Integer> idsFicha, Integer idGrupoTerapeutico) {
        final String query = """
        UPDATE fichas
        SET id_grupo_terapeutico = :id_grupo_terapeutico
        WHERE id = :idFicha
        """;

        jdbi.withHandle(handle -> {
            PreparedBatch batch = handle.prepareBatch(query);
            for (Integer idFicha : idsFicha) {
                batch.bind("id_grupo_terapeutico", idGrupoTerapeutico)
                        .bind("idFicha", idFicha)
                        .add();
            }
            batch.execute();
            return null;
        });

        return findById(idGrupoTerapeutico);
    }

    @Override
    public List<Integer> findGruposTerapeuticosNaoParticipadosPor(Integer idParticipante) {
        final String QUERY = """
                            SELECT id
                            FROM grupos_terapeuticos
                            WHERE id NOT IN (SELECT id_grupo_terapeutico
                            FROM participacao_grupo_terapeutico
                            WHERE id_funcionario = :id_participante)
                            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("id_funcionario", idParticipante)
                .mapTo(Integer.class)
                .list());
    }

    @Override
    public int removerFuncionario(Integer idFuncionario, Integer idGrupoTerapeutico) {
        final String query = """
                DELETE FROM participacao_grupo_terapeutico
                WHERE id_funcionario = :id_funcionario
                AND id_grupo_terapeutico = :id_grupo_terapeutico
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_funcionario", idFuncionario)
                .bind("id_grupo_terapeutico", idGrupoTerapeutico)
                .execute());
    }

    @Override
    public int removerFicha(Integer idFicha) {
        final String query = """
            UPDATE fichas
            SET id_grupo_terapeutico = NULL
            WHERE id = :idFicha
            """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("idFicha", idFicha)
                .execute());
    }

    @Override
    public int delete(Integer idGrupoTerapeutico) {
        final String Delete = """
                DELETE FROM grupos_terapeuticos WHERE id = :id
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(Delete)
                .bind("id", idGrupoTerapeutico)
                .execute());
    }

    @Override
    public int delete(List<Integer> uuids) {
        return 0;
    }
}
