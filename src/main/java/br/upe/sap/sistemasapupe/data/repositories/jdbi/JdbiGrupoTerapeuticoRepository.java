package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jdbi.v3.core.Jdbi;
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
            INSERT INTO grupos_terapeuticos(tema, descricao) VALUES
                (:tema, :descricao)
                RETURNING *
        """;
    }

    @Override
    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico) {
        final String CREATE = createGrupoTerapeuticoSQL();

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
                .bindBean(grupoTerapeutico)
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
                    SET tema = :tema
                    WHERE id = :id
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bindBean(grupoTerapeutico)
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
    public GrupoTerapeutico findById(Integer uid) {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                WHERE id = :id
                """;

        Optional<GrupoTerapeutico> result = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("ui", uid)
                .mapToBean(GrupoTerapeutico.class)
                .findFirst());

        return result.orElse(null);
    }

    @Override
    public List<GrupoTerapeutico> findById(List<Integer> uids) {
        final String QUERY = """
            SELECT * FROM grupos_terapeuticos
            WHERE id IN %s
            """.formatted("<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids",uids)
            .mapToBean(GrupoTerapeutico.class)
            .collectIntoList());
    }

    @Override
    public List<Ficha> findAll() {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findByFuncionario(Integer uidFuncionario) {
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE id = :id LIMIT 1),
                id_participacao AS (
                    SELECT id_grupo_terapeutico AS id_grupo FROM participacao_grupo_terapeutico
                        WHERE id_funcionario = (SELECT id FROM id_func LIMIT 1))
                SELECT * FROM grupos_terapeuticos WHERE id IN (select id_grupo from id_participacao);
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                    .bind("id", uidFuncionario)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findByFicha(Integer idFicha) {
        final String query = """
                WITH id_fic AS (
                    SELECT id FROM fichas WHERE id = :id LIMIT 1),
                id_atendimento AS (
                    SELECT id_atendimento_grupo AS id_grupo FROM ficha_atendimento_grupo
                        WHERE id_ficha = (SELECT id FROM id_fic LIMIT 1))
                SELECT * FROM grupos_terapeuticos WHERE id = (SELECT id_grupo FROM id_atendimento)
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("id", idFicha)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
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
    public GrupoTerapeutico addFuncionario(Integer uidFuncionario, Integer uidGrupoTerapeutico) {
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
                .bind("id_funcionario", uidFuncionario)
                .bind("id_grupo", uidGrupoTerapeutico)
                .execute());

        return findById(uidGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFuncionario(List<Integer> idsFuncionarios, Integer idGrupoTerapeutico) {
        final String query = """
                INSERT INTO coordenacao_atendimento_grupo(id_funcionario, id_atendimento_grupo)
                VALUES (:uids, (SELECT id FROM atendimentos_grupo WHERE :id_grupo = id_grupo_terapeutico))
                """;
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bindList("uids", idsFuncionarios)
                .execute());

        return findById(idGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFicha(Integer uidFicha, Integer uidGrupoTerapeutico) {
        final String query = """
                INSER INTO ficha_atendimento_grupo(id_ficha, id_atendimento_grupo)
                VALUES (:id_ficha, (SELECT id FROM atendimentos_grupo
                                    WHERE id_grupo_terapeutico = :id_grupo))
                """;
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_ficha", uidFicha)
                .bind("id_grupo", uidGrupoTerapeutico)
                .execute());

        return findById(uidGrupoTerapeutico);
    }

    @Override
    public GrupoTerapeutico addFicha(List<Integer> idsFicha, Integer idGrupoTerapeutico) {
        final String query = """
                INSERT INTO ficha_atendimento_grupo(id_ficha, id_atendimento_grupo)
                VALUES (:uids, (SELECT id FROM atendimentos_grupo WHERE :id_grupo = id_grupo_terapeutico))
                """;
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bindList("uids", idsFicha)
                .execute());

        return findById(idGrupoTerapeutico);
    }

    @Override
    public int removerFuncionario(Integer uidFUncionario, Integer uidGrupo) {
        final String query = """
                DELETE FROM coordenacao_atendimento_grupo
                WHERE id_funcionario = :id_func
                AND id_atendimento_grupo = (SELECT id FROM atendimentos_grupo
                                            WHERE id_grupo_terapeutico = :id_grupo)
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_func", uidFUncionario)
                .bind("id_grupo", uidGrupo)
                .execute());
    }

    @Override
    public int removerFicha(Integer uidFicha, Integer uidGrupo) {
        final String query = """
                DELETE FROM ficha_atendimento_grupo
                WHERE id_ficha = :id_fich
                AND id_atendimento_grupo = (SELECT id FROM atendimentos_grupo
                                            WHERE id_grupo_terapeutico = :id_grupo)
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id_fich", uidFicha)
                .bind("id_grupo", uidGrupo)
                .execute());
    }

    @Override
    public int delete(Integer uidGrupoTerapeutico) {
        final String Delete = """
                DELETE FROM grupos_terapeuticos WHERE id = :id
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(Delete)
                .bind("id", uidGrupoTerapeutico)
                .execute());
    }

    @Override
    public int delete(List<Integer> uuids) {
        return 0;
    }
}
