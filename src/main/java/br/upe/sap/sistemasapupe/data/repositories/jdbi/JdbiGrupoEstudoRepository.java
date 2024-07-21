package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {

    Jdbi jdbi;
    FuncionarioRepository funcionarioRepository;

    private final String returningColumns = "id, uid, id_dono dono, tema, descricao";

    private GrupoEstudo mapGrupoEstudo(ResultSet rs, StatementContext stx) throws SQLException {
        return BeanMapper.of(GrupoEstudo.class).map(rs, stx);
    }

    @Override
    public BidiMap<UUID, Integer> findIds(UUID uuid) {
        final String SELECT = "SELECT uid, id FROM grupos_estudo WHERE uid = :uuid LIMIT 1";

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
        final String SELECT = "SELECT uid, id FROM grupos_estudo WHERE uid IN (%s) LIMIT %d"
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
    public GrupoEstudo create(GrupoEstudo grupoEstudo) {
        String CREATE = """
            INSERT INTO grupos_estudo (id_dono, tema, descricao)
            VALUES (:id_dono, :tema, :descricao)
            RETURNING %s
            """.formatted(returningColumns);
        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bind("id_dono", grupoEstudo.getDono())
            .bind("tema", grupoEstudo.getTema())
            .bind("descricao", grupoEstudo.getDescricao())
            .executeAndReturnGeneratedKeys()
            .map(this::mapGrupoEstudo)
            .findFirst()
            .orElse(null));

    }

    @Override
    public List<GrupoEstudo> create(List<GrupoEstudo> grupoEstudos) {
        return grupoEstudos.stream().map(this::create).toList();
    }

    @Override
    public GrupoEstudo update(GrupoEstudo grupoEstudo) {
        final String UPDATE = """
            UPDATE grupos_estudo SET id_dono = :id_dono, tema = :tema, descricao = :descricao
                WHERE id = :id RETURNING %s
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
                .createUpdate(UPDATE)
                .bindBean(grupoEstudo)
                .executeAndReturnGeneratedKeys()
                .map(this::mapGrupoEstudo)
                .findFirst().orElse(null));
    }

    @Override
    public List<GrupoEstudo> update(List<GrupoEstudo> grupoEstudos) {
        return jdbi.inTransaction(handle -> {
            List<GrupoEstudo> result = new ArrayList<>();
            for (GrupoEstudo grupoEstudo : grupoEstudos){ result.add(update(grupoEstudo));
            }
            return result;
        });
    }


    @Override
    public GrupoEstudo findById(Integer id) {
        final String query = """
            SELECT %s FROM grupos_estudo WHERE id = :id
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id)
            .map(this::mapGrupoEstudo)
            .findFirst().orElse(null));
    }

    @Override
    public List<GrupoEstudo> findAll() {
        final String query = "SELECT %s FROM grupos_estudo".formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .mapToBean(GrupoEstudo.class)
            .collectIntoList());
    }

    @Override
    public List<GrupoEstudo> findById(List<Integer> ids) {
        final String query = "SELECT %s FROM grupos_estudo WHERE id IN (%s)"
            .formatted(returningColumns, "<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bindList("ids", ids)
            .mapToBean(GrupoEstudo.class)
            .collectIntoList());
    }

    @Override
    public int delete(Integer id) {
        final String DELETE = "DELETE FROM grupos_estudo WHERE id = :id";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("id", id))
            .execute();
    }

    @Override
    public int delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        final String DELETE = "DELETE FROM grupos_estudo WHERE id IN (<ids>)";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("ids", ids)
            .execute());
    }

    @Override
    public List<GrupoEstudo> findByFuncionario(Integer idFuncionario) {
        final String query = """
            SELECT %s FROM grupos_estudo
                JOIN participacao_grupos_estudo ON id = id_grupo_estudo
                    WHERE id_participante = :id_participante
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id_participante", idFuncionario)
            .mapToBean(GrupoEstudo.class)
            .list());
    }

    @Override
    public Funcionario addFuncionario(Integer idFuncionario, Integer idGrupoEstudo) {
        final String query = """
                INSERT INTO participacao_grupos_estudo(id_grupo_estudo, id_participante)
                VALUES (:id_grupo_estudo, :id_participante)
                RETURNING id_participante
                """;
        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id_participante", idFuncionario)
            .bind("id_grupo_estudo", idGrupoEstudo)
            .map(this::mapFuncionario)
            .findFirst().orElse(null));
    }

    private Funcionario mapFuncionario(ResultSet rs, StatementContext sc) throws SQLException{
        return funcionarioRepository.findById(rs.getInt("id_participante"));
    }

    @Override
    public int deleteParticipacao(int idParticipante) {
        final String DELETE = "DELETE FROM participacao_grupos_estudo WHERE id_participante = :id_participante";

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bind("id_participante", idParticipante)
                .execute());
    }
}


