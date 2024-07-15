package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
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
        return null;
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
                .bind("tempo_inicio", atividade.getTempoInicio())
                .bind("tempo_fim", atividade.getTempoFim())
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
        return null;
    }

    @Override
    public AtendimentoGrupo updateAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo) {
        return null;
    }

    @Override
    public Encontro updateEncontroEstudo(Encontro encontroEstudo) {
        return null;
    }


    @Override
    public Atividade updateStatusAtividade(UUID uidAtividade, StatusAtividade statusAtividade) {
        return null;
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
        final String CREATE = """
                INSERT INTO atividades (id, uid, id_sala, tempo_inicio, tempo_fim)
                VALUES (:id_sala, :tempo_inicio, :tempo_fim)
                RETURNING *
                """
                List<Atividade> resultado = null;
        return null;
    }

    @Override
    public Atividade update(Atividade atividade) {
        return null;
    }

    @Override
    public List<Atividade> update(List<Atividade> atividades) {
        return null;
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
