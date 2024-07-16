package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.AtividadeSalaRepository;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbiAtividadeSalaRepository implements AtividadeSalaRepository {

    Jdbi jdbi;

    public JdbiAtividadeSalaRepository(Jdbi jdbi){
        this.jdbi = jdbi;
    }
    @Override
    public List<Atividade> findBySala(UUID uidSala) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE uid_sala = :uidSala
                """;

        List<Atividade> resultado = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("uid_sala", uidSala)
                .mapToBean(Atividade.class)
                .list());
        return resultado;
    }

    @Override
    public List<Atividade> findByFuncionario(UUID uidFuncionario) {
        // identificar de onde ta vindo a atividade no banco de dados:
        //grupos_estudo, atendimentos_inidividuais, atendimentos_grupos

        final String QUERY = """
                SELECT *
                FROM 
                WHERE 
                """;
        return null;
    }

    @Override
    public List<Atividade> findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE tempo_inicio = :tempo_inicio AND tempo_fim = :tempo_fim
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("tempo_inicio", tempoInicio)
                .bind("tempo_fim", tempoFim)
                .mapToBean(Atividade.class)
                .list());

    }

    @Override
    public List<Atividade> findByStatus(StatusAtividade statusAtividade) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE status = :status
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("status", statusAtividade)
                .mapToBean(Atividade.class)
                .list());
    }

    private Atividade createAtividade (Atividade atividade){
        final String CREATE = """
                INSERT INTO atividades (id, uid, id_sala, tempo_inicio, tempo_fim)
                VALUES (:id_sala, :tempo_inicio, :tempo_fim)
                RETURNING *
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_sala", atividade.getSala().getId())
                .bind("tempo_inicio", atividade.getTempo_inicio())
                .bind("tempo_fim", atividade.getTempo_fim())
                .executeAndReturnGeneratedKeys()
                .mapToBean(Atividade.class)
                .first());
    }
    @Override
    public AtendimentoIndividual createAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual) {
        final String CREATE = """
                INSERT INTO atendimentos_individuais (id, id_ficha, id_terapeuta)
                VALUES (:id_ficha, :id_terapeuta)
                RETURNING *
                """;
        atendimentoIndividual.setId(createAtividade(atendimentoIndividual).getId());
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_ficha", atendimentoIndividual.getFicha())
                .bind("id_terapeuta", atendimentoIndividual.getFuncionario().getId())
                .executeAndReturnGeneratedKeys()
                .mapToBean(AtendimentoIndividual.class)
                .first());
    }

    @Override
    public AtendimentoGrupo createAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo) {
        final String CREATE = """
                INSERT INTO coordenacao_atendimento_grupo (id_funcionario, id_atendimento_grupo)
                VALUES (:id_funcionario, :id_atendimento_grupo)
                RETURNING *
                """;
        atendimentoGrupo.setId(createAtividade(atendimentoGrupo).getId());
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_funcionario", atendimentoGrupo.getGrupoTerapeutico().getCoordenadores())
                .bind("id_atendimento_grupo", atendimentoGrupo.getGrupoTerapeutico().getId())
                .executeAndReturnGeneratedKeys()
                .mapToBean(AtendimentoGrupo.class)
                .first());
    }

    @Override
    public Encontro createEncontroEstudo(Encontro encontroEstudo) {

        final String CREATE = """
                INSERT INTO participacao_grupos_estudo (id_grupo_estudo, id_participante)
                VALUES (:id_grupo_estudo, :id_participante)
                RETURNING *
                """;
        encontroEstudo.setId(createAtividade(encontroEstudo).getId());
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_grupo_estudo", encontroEstudo.getGrupoEstudo().getId())
                .bind("id_participante", encontroEstudo.getGrupoEstudo().getParticipantes())
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
    public Atividade updateStatusAtividade(UUID uidAtividade, StatusAtividade statusAtividade) {
        final String UPDATE = """
                UPDATE atividades
                SET status = :status
                WHERE uid = :uid
                """;

        jdbi.useHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("status", statusAtividade)
                .bind("uid", uidAtividade)
                .execute()
        );

        final String SELECT = """
            SELECT *
            FROM atividades
            WHERE uid = :uid
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(SELECT)
                .bind("uid", uidAtividade)
                .mapToBean(Atividade.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public Sala createSala(Sala sala) {
        final String CREATE = """
                INSERT INTO salas (id, nome, tipo) 
                VALUES (:nome, :tipo)
                RETURNING *
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("nome", sala.getNome())
                .bind("tipo", sala.getTipoSala())
                .executeAndReturnGeneratedKeys()
                .mapToBean(Sala.class)
                .first());
    }

    @Override
    public Atividade create(Atividade atividade) {
        return null;
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
                .bind("tempo_inicio", atividade.getTempo_inicio())
                .bind("tempo_fim", atividade.getTempo_fim())
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
    public Atividade findById(UUID uid) {
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
        List<Atividade> resultado = jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .mapToBean(Atividade.class)
                .list());
        return resultado;
    }

    @Override
    public List<Atividade> findById(List<UUID> uids) {
        final String QUERY = """
                SELECT *
                FROM atividades
                WHERE uids IN (%s)
                """.formatted("<uids>");
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bindList("uids", uids)
                .mapToBean(Atividade.class)
                .list());
    }

    @Override
    public int delete(UUID uid) {
        final String DELETE = """
                DELETE FROM atividades 
                WHERE uid = :uid
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bind("uid", uid)
                .execute());
    }

    @Override
    public int delete(List<UUID> uids) {
        final String DELETE = """
                DELETE FROM atividades
                WHERE uid IN (%s)""".formatted("<uids>");
        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("uids", uids)
                .execute());
    }
}
