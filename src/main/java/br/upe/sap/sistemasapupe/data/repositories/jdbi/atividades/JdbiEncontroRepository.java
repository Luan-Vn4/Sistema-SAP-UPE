package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.EncontroRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
class JdbiEncontroRepository implements EncontroRepository {

    // DEPENDÊNCIAS //
    Jdbi jdbi;

    AuxAtividadeRepository auxRepository;

    FuncionarioRepository funcionarioRepository;


    // CREATE //
    @Override
    public Encontro create(Encontro encontro) {
        final String CREATE = """
            INSERT INTO encontros (id, id_grupo_estudo)
                VALUES (:id, :idGrupoEstudo) RETURNING id, id_grupo_estudo idGrupoEstudo
            """;

        var result = (Encontro) auxRepository.create(encontro);
        return jdbi.withHandle(handle -> handle
            .createQuery(CREATE)
            .bind("id", result.getId())
            .bind("idGrupoEstudo", encontro.getIdGrupoEstudo())
            .map((rs, ctx) -> fillEncontro(rs, ctx, result))
            .findFirst().orElse(null));
    }

    private Encontro fillEncontro(ResultSet rs, StatementContext ctx, Encontro atv) throws SQLException {
        atv.setIdGrupoEstudo(rs.getInt("idGrupoEstudo"));
        atv.setIdsPresentes(findIdsComparecidos(atv.getId()));
        return atv;
    }

    @Override
    public List<Encontro> findByGrupoEstudo(Integer idGrupoEstudo) {
        final String QUERY = """
                SELECT encontros.id_grupo_estudo, %s
                FROM encontros
                INNER JOIN atividades ON encontros.id = atividades.id
                WHERE encontros.id_grupo_estudo = :id_grupo_estudo
                """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("id_grupo_estudo", idGrupoEstudo)
                .map(this::mapEncontro)
                .list());
    }

    @Override
    public List<Encontro> create(List<Encontro> encontros) {
        return encontros.stream().map(this::create).toList();
    }

    @Override
    public int addComparecimento(int idFuncionario, int idEncontro) {
        final String INSERT = """
            INSERT INTO comparecimento_encontros(id_encontro, id_participante) VALUES
                (:idEncontro, :idFuncionario)
                    RETURNING id_participante idParticipante
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(INSERT)
            .bind("idEncontro", idEncontro)
            .bind("idFuncionario", idFuncionario)
            .executeAndReturnGeneratedKeys()
            .mapTo(Integer.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Integer> addComparecimentos(List<Integer> idsFuncionarios,
                                            int idEncontro) {
        return idsFuncionarios.stream()
            .map(x -> addComparecimento(x, idEncontro))
            .toList();
    }


    // UPDATE //
    @Override
    public Encontro update(Encontro encontro) {
        return jdbi.inTransaction(handle -> {
            int id = encontro.getId();
            List<Integer> presentes = encontro.getIdsPresentes();

            deleteAllComparecidos(id);
            presentes = addComparecimentos(presentes, id);

            var result = (Encontro) auxRepository.update(encontro);
            result.setIdsPresentes(presentes);

            return result;
        });
    }

    @Override
    public List<Encontro> update(List<Encontro> encontros) {
        return encontros.stream().map(this::update).toList();
    }


    // READ //
    @Override
    public Encontro findById(Integer id) {
        final String QUERY = """
            SELECT id_grupo_estudo idGrupoEstudo, %s
                FROM encontros INNER JOIN atividades
                    ON atividades.id = :id AND encontros.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map(this::mapEncontro)
            .findFirst().orElse(null));
    }

    public Encontro mapEncontro(ResultSet rs, StatementContext ctx) throws SQLException {
        var atividade = BeanMapper.of(Encontro.class).map(rs, ctx);
        auxRepository.fillAtividadeFields(atividade, rs);
        fillEncontro(rs, ctx, atividade);
        return atividade;
    }

    @Override
    public List<Encontro> findAll() {
        final String QUERY = """
            SELECT id_grupo_estudo, %s
                FROM encontros INNER JOIN atividades
                    ON encontros.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapEncontro)
            .collectIntoList());
    }

    @Override
    public List<Encontro> findById(List<Integer> ids) {
        final String QUERY = """
            SELECT id_grupo_estudo, %s
                FROM encontros INNER JOIN atividades
                    ON atividades.id IN (%s) AND encontros.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns, "<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids", ids)
            .map(this::mapEncontro)
            .collectIntoList());
    }

    @Override
    public List<Integer> findIdsComparecidos(int idEncontro) {
        final String SELECT = """
            SELECT id_participante FROM comparecimento_encontros
                WHERE id_encontro = :id;
        """;

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bind("id", idEncontro)
            .mapTo(Integer.class)
            .collectIntoList());
    }


    // DELETE //
    @Override
    public int delete(Integer id) {
        return auxRepository.delete(id);
    }

    @Override
    public int delete(List<Integer> ids) {
        return auxRepository.delete(ids);
    }

    @Override
    public int deleteComparecido(int idFuncionario, int idEncontro) {
        final String DELETE = """
            DELETE FROM comparecimento_encontros
                WHERE id_participante = :idFuncionario AND id_encontro = :idEncontro
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("idFuncionario", idFuncionario)
            .bind("idEncontro", idEncontro)
            .execute());
    }

    @Override
    public int deleteComparecidos(List<Integer> idsFuncionarios, int idEncontro) {
        final String DELETE = """
            DELETE FROM comparecimento_encontros
                WHERE id_participante IN (%s) AND id_encontro = :idEncontro
            """.formatted("<idsFuncionarios>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("idsFuncionarios", idsFuncionarios)
                .bind("idEncontro", idEncontro)
                .execute());
    }

    private void deleteAllComparecidos(int id) {
        final String REMOVE_ALL_COMPARECIMENTOS = """
            DELETE FROM comparecimento_encontros
                WHERE id_encontro = :idEncontro
            """;

        jdbi.withHandle(handle -> handle
            .createUpdate(REMOVE_ALL_COMPARECIMENTOS)
            .bind("idEncontro", id)
            .execute());
    }

}
