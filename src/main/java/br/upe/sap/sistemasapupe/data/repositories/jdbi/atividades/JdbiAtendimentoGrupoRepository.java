package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtendimentoGrupoRepository;
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
class JdbiAtendimentoGrupoRepository implements AtendimentoGrupoRepository {

    // DEPENDÊNCIAS //
    Jdbi jdbi;

    AuxAtividadeRepository auxRepository;

    FuncionarioRepository funcionarioRepository;

    FichaRepository fichaRepository;


    // CREATE //
    @Override
    public AtendimentoGrupo create(AtendimentoGrupo atendimentoGrupo) {
        final String CREATE = """
            INSERT INTO atendimentos_grupo (id, id_grupo_terapeutico)
                VALUES (:id, :idGrupoTerapeutico) RETURNING id, id_grupo_terapeutico idGrupoTerapeutico
            """;

        var result = (AtendimentoGrupo) auxRepository.create(atendimentoGrupo);
        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bind("id", result.getId())
            .bind("idGrupoTerapeutico", atendimentoGrupo.getIdGrupoTerapeutico())
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> fillAtendimentoGrupo(rs, ctx, result))
            .findFirst().orElse(null));
    }

    private AtendimentoGrupo fillAtendimentoGrupo(ResultSet rs, StatementContext ctx,
                                                  AtendimentoGrupo atv) throws SQLException {
        atv.setIdGrupoTerapeutico(rs.getInt("idGrupoTerapeutico"));
        atv.setIdsMinistrantes(findIdsMinistrantes(atv.getId()));
        atv.setIdsParticipantes(findIdsParticipantes(atv.getId()));
        return atv;
    }

    @Override
    public List<AtendimentoGrupo> create(List<AtendimentoGrupo> atendimentoGrupos) {
        return atendimentoGrupos.stream().map(this::create).toList();
    }

    @Override
    public int addMinistrante(Integer idFuncionario, int idAtendimentoGrupo) {
        final String INSERT = """
            INSERT INTO coordenacao_atendimento_grupo(id_funcionario, id_atendimento_grupo)
                VALUES (:idFuncionario, :idAtendimentoGrupo)
                    RETURNING id_funcionario idFuncionario
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(INSERT)
            .bind("idFuncionario",idFuncionario)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .executeAndReturnGeneratedKeys()
            .mapTo(Integer.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Integer> addMinistrantes(List<Integer> idsFuncionarios,
                                         int idAtendimentoGrupo) {
        return idsFuncionarios.stream()
            .map(x -> addMinistrante(x, idAtendimentoGrupo))
            .toList();
    }

    @Override
    public int addParticipante(int idFicha, int idAtendimentoGrupo) {
        final String INSERT = """
            INSERT INTO ficha_atendimento_grupo(id_ficha, id_atendimento_grupo)
                VALUES (:idFicha, :idAtendimentoGrupo)
                RETURNING id_ficha idFicha
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(INSERT)
            .bind("idFicha", idFicha)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .executeAndReturnGeneratedKeys()
            .mapTo(Integer.class)
            .findFirst().orElse(null));
    }

    @Override
    public List<Integer> addParticipantes(List<Integer> idsFichas,
                                          int idAtendimentoGrupo) {
        return idsFichas.stream()
            .map(x -> addParticipante(x, idAtendimentoGrupo))
            .toList();
    }


    // UPDATE //
    @Override
    public AtendimentoGrupo update(AtendimentoGrupo atendimentoGrupo) {
        return jdbi.inTransaction(handle -> {
            int id = atendimentoGrupo.getId();
            List<Integer> idsMinistrantes = atendimentoGrupo.getIdsMinistrantes();
            List<Integer> idsParticipantes = atendimentoGrupo.getIdsParticipantes();

            deleteAllMinistrantes(id);
            deleteAllParticipantes(id);

            idsMinistrantes = addMinistrantes(idsMinistrantes, id);
            idsParticipantes = addParticipantes(idsParticipantes, id);

            var result = (AtendimentoGrupo) auxRepository.update(atendimentoGrupo);
            result.setIdsMinistrantes(idsMinistrantes);
            result.setIdsParticipantes(idsParticipantes);

            return result;
        });
    }

    @Override
    public List<AtendimentoGrupo> update(List<AtendimentoGrupo> atendimentoGrupos) {
        return atendimentoGrupos.stream().map(this::update).toList();
    }


    // READ //
    @Override
    public AtendimentoGrupo findById(Integer id) {
        final String QUERY = """
            SELECT id_grupo_terapeutico idGrupoTerapeutico, %s
                FROM atendimentos_grupo INNER JOIN atividades
                    ON atividades.id = :id AND atendimentos_grupo.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map(this::mapAtendimentoGrupo)
            .findFirst().orElse(null));
    }

    private AtendimentoGrupo mapAtendimentoGrupo(ResultSet rs, StatementContext ctx) throws SQLException {
        AtendimentoGrupo atividade = BeanMapper.of(AtendimentoGrupo.class).map(rs,ctx);
        auxRepository.fillAtividadeFields(atividade, rs);
        fillAtendimentoGrupo(rs, ctx, atividade);
        return atividade;
    }

    @Override
    public List<AtendimentoGrupo> findAll() {
        final String QUERY = """
            SELECT id_grupo_terapeutico idGrupoTerapeutico, %s
                FROM atendimentos_grupo INNER JOIN atividades
                    ON atendimentos_grupo.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapAtendimentoGrupo)
            .collectIntoList());
    }

    @Override
    public List<AtendimentoGrupo> findById(List<Integer> ids) {
        final String QUERY = """
            SELECT id_grupo_terapeutico idGrupoTerapeutico, %s
                FROM atendimentos_grupo INNER JOIN atividades
                    ON atividades.id IN (%s) AND atendimentos_grupo.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns, "<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids", ids)
            .map(this::mapAtendimentoGrupo)
            .collectIntoList());
    }

    @Override
    public List<Integer> findIdsMinistrantes(int idAtendimentoGrupo) {
        final String SELECT = """
            SELECT id_funcionario FROM coordenacao_atendimento_grupo
                WHERE id_atendimento_grupo = :id;
        """;

        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT)
            .bind("id", idAtendimentoGrupo)
            .mapTo(Integer.class)
            .collectIntoList());
    }

    @Override
    public List<Integer> findIdsParticipantes(int idAtendimentoGrupo) {
        final String SELECT = """
            SELECT id_ficha FROM ficha_atendimento_grupo
                WHERE id_atendimento_grupo = :id
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bind("id", idAtendimentoGrupo)
            .mapTo(Integer.class)
            .collectIntoList());
    }


    // DELETE //
    @Override
    public int delete(Integer id) {
        return auxRepository.delete(id);
    }

    @Override
    public int delete(List<Integer> integers) {
        return auxRepository.delete(integers);
    }

    @Override
    public int deleteMinistrante(int idMinistrante, int idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM coordenacao_atendimento_grupo WHERE
                id_funcionario = :idMinistrante AND id_atendimento_grupo = :idAtendimentoGrupo
            """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bind("idMinistrante", idMinistrante)
                .bind("idAtendimentoGrupo", idAtendimentoGrupo)
                .execute());
    }

    @Override
    public int deleteMinistrantes(List<Integer> idsMinistrantes,
                                  int idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM coordenacao_atendimento_grupo WHERE
                id_funcionario IN (%s) AND id_atendimento_grupo = :idAtendimentoGrupo
            """.formatted("<idsMinistrantes>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("idsMinistrantes", idsMinistrantes)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .execute());
    }

    @Override
    public int deleteParticipante(int idFicha, int idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM ficha_atendimento_grupo WHERE
                id_ficha = :idFicha AND id_atendimento_grupo = :idAtendimentoGrupo
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("idFicha", idFicha)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .execute());
    }

    @Override
    public int deleteParticipantes(List<Integer> idsParticipantes, int idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM ficha_atendimento_grupo WHERE
                id_ficha IN (%s) AND id_atendimento_grupo = :idAtendimentoGrupo
            """.formatted("<idsParticipantes>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("idsParticipantes", idsParticipantes)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .execute());
    }

    private void deleteAllMinistrantes(Integer idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM coordenacao_atendimento_grupo WHERE
                id_atendimento_grupo = :idAtendimentoGrupo
            """;

        jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .execute());
    }

    private void deleteAllParticipantes(Integer idAtendimentoGrupo) {
        final String DELETE = """
            DELETE FROM ficha_atendimento_grupo
                WHERE id_atendimento_grupo = :idAtendimentoGrupo
            """;

        jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("idAtendimentoGrupo", idAtendimentoGrupo)
            .execute());
    }

}
