package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.AtividadesRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.SalaRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbiAtividadesRepository implements AtividadesRepository {

    Jdbi jdbi;

    FuncionarioRepository funcionarioRepository;

    SalaRepository salaRepository;

    FichaRepository fichaRepository;

    final String returningAtividadeColumns = "atividades.id, atividades.uid, atividades.id_sala, " +
            "atividades.id_funcionario, atividades.tempo_inicio, atividades.tempo_fim, atividades.status";

    @Override
    public AtendimentoIndividual findAtendimentoIndividual(Integer id) {
        final String QUERY = """
            SELECT id_ficha, id_terapeuta, %s
                FROM atendimentos_individuais INNER JOIN atividades
                    ON atividades.id = :id AND atendimentos_individuais.id = atividades.id
        """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map((rs, ctx) -> {
                var atividade =
                    BeanMapper.of(AtendimentoIndividual.class).map(rs, ctx);

                fillAtividadeFields(atividade, rs);
                atividade.setFicha(
                    fichaRepository.findById(rs.getInt("id_ficha")));
                atividade.setTerapeuta(atividade.getFuncionario());

                return atividade;
            })
            .findFirst().orElse(null));
    }

    private void fillAtividadeFields(Atividade atividade, ResultSet rs) throws SQLException {
        atividade.setSala(salaRepository.findById(
                rs.getInt("id_sala")));
        atividade.setFuncionario(funcionarioRepository.findById(
                rs.getInt("id_funcionario")));
    }

    public AtendimentoGrupo findAtendimentoGrupo(Integer id) {
        final String QUERY = """
            SELECT id_grupo_terapeutico, %s
                FROM atendimentos_grupo INNER JOIN atividades
                    ON atividades.id = :id AND atendimentos_grupo.id = atividades.id
        """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map((rs, ctx) -> {
                var atividade = BeanMapper.of(AtendimentoGrupo.class).map(rs,ctx);

                // TO DO
                fillAtividadeFields(atividade, rs);
                atividade.setGrupoTerapeutico(null);
                atividade.setParticipantes(findParticipantes(
                    atividade.getId()));
                atividade.setMinistrantes(findMinistrantes(
                    atividade.getId()));

                return atividade;
            })
            .findFirst().orElse(null));
    }

    public List<Funcionario> findMinistrantes(Integer idAtendimentoGrupo) {
        final String SELECT = """
            SELECT id_funcionario FROM coordenacao_atendimento_grupo
                WHERE id_atendimento_grupo = :id;
        """;

        return jdbi.withHandle(handle -> {
            List<Integer> ids = handle.createQuery(SELECT)
                .bind("id", idAtendimentoGrupo)
                .mapTo(Integer.class)
                .collectIntoList();

            return funcionarioRepository.findById(ids);
        });
    }

    public List<Ficha> findParticipantes(Integer idAtendimentoGrupo) {
        final String SELECT = """
            SELECT id_ficha FROM ficha_atendimento_grupo
                WHERE id_atendimento_grupo = :id
            """;

        return jdbi.withHandle(handle -> {
            List<Integer> ids = handle.createQuery(SELECT)
                .bind("id", idAtendimentoGrupo)
                .mapTo(Integer.class)
                .collectIntoList();

            return fichaRepository.findById(ids);
        });
    }

    public Encontro findEncontro(Integer idEncontro) {
        final String QUERY = """
            SELECT id_grupo_estudo, %s
                FROM encontros INNER JOIN atividades
                    ON atividades.id = :id AND encontros.id = atividades.id
        """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", idEncontro)
            .map((rs, ctx) -> {
                var atividade = BeanMapper.of(Encontro.class).map(rs, ctx);

                // TO DO
                fillAtividadeFields(atividade, rs);
                atividade.setGrupoEstudo(null);
                atividade.setPresentes(findPresentesEncontro(
                    atividade.getId()));

                return atividade;
            })
            .findFirst().orElse(null));
    }

    public List<Funcionario> findPresentesEncontro(Integer idEncontro) {
        final String SELECT = """
            SELECT id_participante FROM comparecimento_encontros
                WHERE id_encontro = :id;
        """;

        return jdbi.withHandle(handle -> {
            List<Integer> ids = handle.createQuery(SELECT)
                .bind("id", idEncontro)
                .mapTo(Integer.class)
                .collectIntoList();

            return funcionarioRepository.findById(ids);
        });
    }

    @Override
    public List<Atividade> findBySala(Integer idSala) {
        final String QUERY = "SELECT id FROM atividades WHERE id_sala = :id_sala";

        List<Integer> ids = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id_sala", idSala)
            .mapTo(Integer.class)
            .collectIntoList());

        return findById(ids);
    }

    @Override
    public List<Atividade> findByFuncionario(Integer idFuncionario) {
        if (idFuncionario == null) throw new IllegalArgumentException("UID do funcionário não deve ser nulo");

        final String SELECT = "SELECT id FROM atividades WHERE id_funcionario = :id_funcionario";

        List<Integer> ids = jdbi.withHandle(handle -> handle
            .createQuery(SELECT)
            .bind("id_funcionario", idFuncionario)
            .mapTo(Integer.class)
            .collectIntoList());

        return findById(ids);
    }

    @Override
    public List<Atividade> findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim) {
        final String QUERY = """
            SELECT id FROM atividades
                WHERE (tempo_inicio BETWEEN :tempo_inicio AND :tempo_fim)
                    AND (tempo_fim BETWEEN :tempo_inicio AND :tempo_fim)
            """;

        List<Integer> ids = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("tempo_inicio", tempoInicio)
                .bind("tempo_fim", tempoFim)
                .mapToBean(Integer.class)
                .collectIntoList());

        return findById(ids);
    }

    @Override
    public List<Atividade> findByStatus(StatusAtividade statusAtividade) {
        final String QUERY = """
            SELECT id FROM atividades
                WHERE status = CAST(:status AS status_atividade)
            """;

        List<Integer> ids = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("status", statusAtividade)
            .mapToBean(Integer.class)
            .collectIntoList());

        return findById(ids);
    }

    private Atividade createAtividade (Atividade atividade) {
        final String CREATE = """
            INSERT INTO atividades (id_funcionario, id_sala, tempo_inicio, tempo_fim, status)
                VALUES (:idAutor, :idSala, :tempoInicio, :tempoFim, :status)
                    RETURNING %s
            """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bindBean(atividade)
            .bind("idSala", atividade.getSala().getId())
            .bind("idAutor", atividade.getFuncionario().getId())
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> {
                var result = BeanMapper.of(Atividade.class).map(rs, ctx);
                fillAtividadeFields(result, rs);
                return result;
            })
            .findFirst().orElse(null));
    }

    @Override
    public AtendimentoIndividual createAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual) {
        final String CREATE = """
            INSERT INTO atendimentos_individuais (id, id_ficha, id_terapeuta)
            VALUES (:id, :idFicha, :idTerapeuta)
            RETURNING id, id_ficha, id_terapeuta
            """;

        Atividade atividade = createAtividade(atendimentoIndividual);
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("idFicha", atendimentoIndividual.getFicha())
                .bind("idTerapeuta", atendimentoIndividual.getFuncionario().getId())
                .executeAndReturnGeneratedKeys()
                .map((rs, ctx) -> mapAtendimentoIndividual(rs, ctx, atividade))
                .findFirst().orElse(null));
    }

    private AtendimentoIndividual mapAtendimentoIndividual(ResultSet rs, StatementContext ctx,
                                                           Atividade atv) throws SQLException{
        return AtendimentoIndividual.builder()
                .id(atv.getId()).uid(atv.getUid())
                .sala(atv.getSala())
                .tempoFim(atv.getTempoFim())
                .tempoInicio(atv.getTempoInicio())
                .statusAtividade(atv.getStatus())
                .ficha(fichaRepository.findById(rs.getInt("id_ficha")))
                .terapeuta(atv.getFuncionario())
                .build();
    }

    @Override
    public AtendimentoGrupo createAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo) {
        final String CREATE = """
            INSERT INTO atendimentos_grupo (id, id_grupo_terapeutico)
                VALUES (:id, :idGrupoTerapeutico)
            """;

        Atividade atividade = createAtividade(atendimentoGrupo);
        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bind("id_funcionario", atendimentoGrupo.getGrupoTerapeutico().getCoordenadores())
            .bind("id_atendimento_grupo", atendimentoGrupo.getGrupoTerapeutico().getId())
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> mapAtendimentoGrupo(rs, ctx, atividade))
            .findFirst().orElse(null));
    }

    private AtendimentoGrupo mapAtendimentoGrupo(ResultSet rs, StatementContext ctx,
                                                 Atividade atv) throws SQLException{
        return AtendimentoGrupo.builder()
                .id(atv.getId()).uid(atv.getUid())
                .sala(atv.getSala())
                .tempoFim(atv.getTempoFim())
                .tempoInicio(atv.getTempoInicio())
                .statusAtividade(atv.getStatus())
                .ministrantes(findMinistrantes(atv.getId()))
                .participantes(findParticipantes(atv.getId()))
                .build();
    }

    @Override
    public Encontro createEncontroEstudo(Encontro encontroEstudo) {
        final String CREATE = """
            INSERT INTO grupos_estudo (id_dono, tema)
                VALUES (:idGrupoEstudo, :idParticipante)
            """;

        // NÃO PRONTO
        Atividade atividade = createAtividade(encontroEstudo);
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id", encontroEstudo.getGrupoEstudo().getId())
                .bind("tema", encontroEstudo.getGrupoEstudo().getParticipantes())
                .executeAndReturnGeneratedKeys()
                .mapToBean(Encontro.class)
                .first());
    }

    @Override
    public AtendimentoIndividual updateAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual) {
        update(atendimentoIndividual);

        final String UPDATE = """
                UPDATE atendimentos_individuais
                SET id_ficha = :id_ficha,
                id_terapeuta = :id_terapeuta
                WHERE id = :id
                """;
        jdbi.useHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("id_ficha", atendimentoIndividual.getFicha().getId())
                .bind("id_terapeuta", atendimentoIndividual.getFuncionario().getId())
                .execute()
        );

        final String SELECT = """
            SELECT *
            FROM atendimentos_individuais
            WHERE id = :id
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("id", atendimentoIndividual.getId())
                .mapToBean(AtendimentoIndividual.class)
                .findFirst()
                .orElse(null));


    }

    @Override
    public AtendimentoGrupo updateAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo) {

        return jdbi.inTransaction(handle -> {

            update(atendimentoGrupo);

            // Remove todos os registros de participantes
            final String REMOVE_PARTICIPANTES = """
                DELETE FROM ficha_atendimento_grupo
                WHERE id_atendimento_grupo = :id
                """;

            handle.createUpdate(REMOVE_PARTICIPANTES)
                    .bind("id", atendimentoGrupo.getId())
                    .execute();

            // Reinsere os participantes
            final String INSERT_PARTICIPANTES = """
                INSERT INTO ficha_atendimento_grupo (id_ficha, id_atendimento_grupo)
                VALUES (:id_ficha, :id_atendimento_grupo)
                """;

            for (Ficha participante : atendimentoGrupo.getParticipantes()) {
                handle.createUpdate(INSERT_PARTICIPANTES)
                        .bind("id_ficha", participante.getId())
                        .bind("id_atendimento_grupo", atendimentoGrupo.getId())
                        .executeAndReturnGeneratedKeys();
            }

            // Remove todos os registros dos ministrantes
            final String REMOVE_MINISTRANTES = """
                DELETE FROM coordenacao_atendimento_grupo
                WHERE id_atendimento_grupo = :id
                """;

            handle.createUpdate(REMOVE_MINISTRANTES)
                    .bind("id", atendimentoGrupo.getId())
                    .execute();

            // Reinsere os ministrantes
            final String INSERT_MINISTRANTES = """
                INSERT INTO coordenacao_atendimento_grupo (id_funcionario, id_atendimento_grupo) 
                VALUES (:id_funcionario, :id_atendimento_grupo)
                """;

            for (Funcionario ministrante : atendimentoGrupo.getMinistrantes()) {
                handle.createUpdate(INSERT_MINISTRANTES)
                        .bind("id_funcionario", ministrante.getId())
                        .bind("id_atendimento_grupo", atendimentoGrupo.getId())
                        .executeAndReturnGeneratedKeys();
            }

            return atendimentoGrupo;
        });
    }


    @Override
    public Encontro updateEncontroEstudo(Encontro encontroEstudo) {
        return jdbi.inTransaction(handle -> {
            update(encontroEstudo);

            final String REMOVE_COMPARECIMENTO = """
                    DELETE FROM comparecimento_encontros
                    WHERE id_encontro = :id_encontro
                    """;
            handle.createUpdate(REMOVE_COMPARECIMENTO)
                    .bind("id_encontro", encontroEstudo.getId())
                    .execute();

            final String INSERT_COMPARECIMENTO = """
                    INSERT INTO comparecimento_encontros (id_encontro, id_participante)
                    VALUES (:id_encontro, :id_participante)
                    """;
            for (Funcionario participante : encontroEstudo.getGrupoEstudo().getParticipantes()){
                handle.createUpdate(INSERT_COMPARECIMENTO)
                        .bind("id_encontro", encontroEstudo.getGrupoEstudo().getId())
                        .bind("id_participante", participante.getId())
                        .executeAndReturnGeneratedKeys();
            }

            return encontroEstudo;

        });
    }


    @Override
    public Atividade updateStatusAtividade(Integer idAtividade, StatusAtividade statusAtividade) {
        final String UPDATE = """
                UPDATE atividades
                SET status = :status
                WHERE id = :id
                """;

        jdbi.useHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("status", statusAtividade)
                .bind("id", idAtividade)
                .execute()
        );

        final String SELECT = """
            SELECT *
            FROM atividades
            WHERE id = :id
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("id", idAtividade)
                .mapToBean(Atividade.class)
                .findFirst()
                .orElse(null));
    }


    @Override
    public Atividade create(Atividade atividade){
        if (atividade instanceof AtendimentoIndividual) {
            return createAtendimentoIndividual((AtendimentoIndividual) atividade);
        } else if (atividade instanceof AtendimentoGrupo) {
            return createAtendimentoGrupo((AtendimentoGrupo) atividade);
        } else {
            return createEncontroEstudo((Encontro) atividade);
        }
    }

    @Override
    public List<Atividade> create(List<Atividade> atividades) {
        return atividades.stream().map(this::createAtividade).toList();
    }

    @Override
    public Atividade update(Atividade atividade) {
        final String UPDATE = """
                UPDATE atividades
                SET id_sala = :id_sala,
                tempo_inicio = :tempo_inicio,
                tempo_fim = :tempo_fim,
                status = :status
                WHERE id = :id
                """;
        jdbi.useHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("id_sala", atividade.getSala().getId())
                .bind("tempo_inicio", atividade.getTempoInicio())
                .bind("tempo_fim", atividade.getTempoFim())
                .bind("status", atividade.getStatus())
                .execute()
        );

        final String SELECT = """
            SELECT *
            FROM atividades
            WHERE id = :id
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("id", atividade.getId())
                .mapToBean(Atividade.class)
                .findFirst()
                .orElse(null));
    }


    @Override
    public List<Atividade> update(List<Atividade> atividades) {
        throw new UnsupportedOperationException("Atualizações em lote não são suportadas.");
    }

    @Override
    public Atividade findById(Integer uid) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE uid = :uid
                """;
        Optional<Atividade> resultado = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("uid", uid )
                .mapToBean(Atividade.class)
                .findFirst());
        return resultado.orElse(null);
    }

    @Override
    public List<Atividade> findAll() {
        final String QUERY = """
                SELECT *
                FROM atividades
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .mapToBean(Atividade.class)
                .list());
    }

    @Override
    public List<Atividade> findById(List<Integer> ids) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE uids IN (%s)
                """.formatted("<uids>");
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bindList("ids", ids)
                .mapToBean(Atividade.class)
                .list());
    }

    @Override
    public int delete(Integer id) {
        final String DELETE = """
                DELETE FROM atividades
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
                DELETE FROM atividades
                WHERE id IN (%s)""".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }

}
