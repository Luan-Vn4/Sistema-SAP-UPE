package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
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


@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {

    Jdbi jdbi;
    FuncionarioRepository funcionarioRepository;

    private final String returningColumns = "id, uid, id_dono, tema, descricao";

    private GrupoEstudo mapGrupoEstudo(ResultSet rs, StatementContext stx) throws SQLException {
        GrupoEstudo grupoEstudo = BeanMapper.of(GrupoEstudo.class).map(rs, stx);
        grupoEstudo.setDono(funcionarioRepository.findById(rs.getInt("id_dono")));
        return grupoEstudo;
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
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_dono", grupoEstudo.getDono().getId())
                .bind("tema", grupoEstudo.getTemaEstudo())
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
        final String UPDATE = "UPDATE grupos_estudo SET id_dono = :id_dono, tema = :tema, descricao = :descricao WHERE id = :id RETURNING %s"
                .formatted(returningColumns);

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
        return null;
    }

    @Override
    public List<GrupoEstudo> findAll() {
        return null;
    }

    @Override
    public List<GrupoEstudo> findById(List<Integer> ids) {
        return null;
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
        final String DELETE = "DELETE FROM grupos_estudo WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }

    @Override
    public GrupoEstudo findByFuncionario(int idFuncionario) {
        return null;
    }

    @Override
    public Funcionario addFuncionario(Funcionario funcionario) {
        return null;
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


