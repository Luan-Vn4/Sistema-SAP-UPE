package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
<<<<<<< HEAD
=======
import lombok.AllArgsConstructor;
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
<<<<<<< HEAD

=======
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

<<<<<<< HEAD

=======
@AllArgsConstructor
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {

    Jdbi jdbi;
    FuncionarioRepository funcionarioRepository;

    private final String returningColumns = "id, uid, id_dono, tema, descricao";

    private GrupoEstudo mapGrupoEstudo(ResultSet rs, StatementContext stx) throws SQLException {
<<<<<<< HEAD
        GrupoEstudo grupoEstudo = BeanMapper.of(GrupoEstudo.class).map(rs, stx);
        grupoEstudo.setDono(funcionarioRepository.findById(rs.getInt("id_dono")));
        return grupoEstudo;
=======
        return BeanMapper.of(GrupoEstudo.class).map(rs, stx);
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
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
<<<<<<< HEAD
                .bind("id_dono", grupoEstudo.getDono().getId())
                .bind("tema", grupoEstudo.getTemaEstudo())
=======
                .bind("id_dono", grupoEstudo.getDono())
                .bind("tema", grupoEstudo.getTema())
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
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
        final String query = """
<<<<<<< HEAD
                SELECT *
                FROM grupos_estudo
                WHERE id = :id
=======
                SELECT id, uid, tema, descricao, id_dono FROM grupos_estudo WHERE id = :id
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("id", id)
<<<<<<< HEAD
                .mapToBean(GrupoEstudo.class)
=======
                .map(this::mapGrupoEstudo)
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
                .findFirst().orElse(null));
    }

    @Override
    public List<GrupoEstudo> findAll() {
        final String query = """
                SELECT *
                FROM grupos_estudo
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .mapToBean(GrupoEstudo.class)
                .collectIntoList());
    }

    @Override
    public List<GrupoEstudo> findById(List<Integer> ids) {
        final String query = """
                SELECT *
                FROM grupos_estudo
<<<<<<< HEAD
                WHERE id IN (<ids>)
                """;
=======
                WHERE id IN (%s)
                """.formatted("<ids>");
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
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
<<<<<<< HEAD
        final String DELETE = "DELETE FROM grupos_estudo WHERE id IN (%s)".formatted("<ids>");
=======
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        final String DELETE = "DELETE FROM grupos_estudo WHERE id IN (<ids>)";
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }

    @Override
    public GrupoEstudo findByFuncionario(Integer idFuncionario) {
        final String query = """
<<<<<<< HEAD
                SELECT id_grupo_estudo
                FROM participacao_grupos_estudo
                WHERE id_participante = :id_funcionario
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("id_funcionario", idFuncionario)
=======
            SELECT g.id, g.uid, g.tema, g.descricao, g.id_dono
            FROM grupos_estudo g
            JOIN participacao_grupos_estudo pge ON g.id = pge.id_grupo_estudo
            WHERE pge.id_participante = :id_participante
            """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("id_participante", idFuncionario)
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
                .mapToBean(GrupoEstudo.class)
                .findFirst().orElse(null));
    }

    @Override
    public Funcionario addFuncionario(Integer idFuncionario, Integer idGrupoEstudo) {
        final String query = """
                INSERT INTO participacao_grupos_estudo(id_grupo_estudo, id_participante)
<<<<<<< HEAD
                VALUES (:idGrupoEstudo, :id_funcionario)
=======
                VALUES (:id_grupo_estudo, :id_participante)
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
                RETURNING id_participante
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
<<<<<<< HEAD
                .bind("id_funcionario", idFuncionario)
                .bind("idGrupoEstudo", idGrupoEstudo)
=======
                .bind("id_participante", idFuncionario)
                .bind("id_grupo_estudo", idGrupoEstudo)
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
                .map(this::mapFuncionario)
                .findFirst().orElse(null));
    }

    private Funcionario mapFuncionario(ResultSet rs, StatementContext sc) throws SQLException{
<<<<<<< HEAD
        return funcionarioRepository.findById(rs.getInt("id_funcionario"));
=======
        return funcionarioRepository.findById(rs.getInt("id_participante"));
>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
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


