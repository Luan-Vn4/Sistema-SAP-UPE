package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbiGrupoTerapeuticoRepository implements GrupoTerapeuticoRepository {
    Jdbi jdbi;

    public JdbiGrupoTerapeuticoRepository(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    private String createGrupoTerapeuticoSQL(){
        return """
            INSERT INTO grupos_terapeuticos(tema) VALUES
                (:tema)
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
                    WHERE uid = CAST (:uid AS UUID)
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

    @Override
    public GrupoTerapeutico findById(UUID uid) {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                WHERE uid = CAST(:uid AS UUID)
                """;

        Optional<GrupoTerapeutico> result = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("uid", uid)
                .mapToBean(GrupoTerapeutico.class)
                .findFirst());

        return result.orElse(null);
    }

    @Override
    public List<GrupoTerapeutico> findById(List<UUID> uids) {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                WHERE uid IN <uids>
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("uids",uids)
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
                .list());
    }
    // revisado
    @Override
    public List<GrupoTerapeutico> findByFuncionario(UUID uidFuncionario) {
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE uid = CAST(:uid AS UUID) LIMIT 1),
                id_participacao AS (
                    SELECT id_grupo_terapeutico AS id_grupo FROM participacao_grupo_terapeutico
                        WHERE id_funcionario = (SELECT id FROM id_func LIMIT 1))
                SELECT * FROM grupos_terapeuticos WHERE id IN (select id_grupo from id_participacao)
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("uid", uidFuncionario)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    // revisado
    @Override
    public List<GrupoTerapeutico> findByFicha(UUID idFicha) {
        final String query = """
                WITH id_fic AS (
                    SELECT id FROM fichas WHERE uid = CAST(:uid AS UUID) LIMIT 1),
                SELECT * FROM grupos_terapeuticos WHERE id IN (
                    SELECT id_grupo_terapeutico FROM id_fic)
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("uid", idFicha)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    // revisado
    @Override
    public GrupoTerapeutico addFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico) {
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE uid = CAST(:uid_funcionario AS UUID) LIMIT 1),
                id_grupo AS (
                    SELECT id FROM grupos_terapeuticos WHERE uid = CAST(:uid_grupo_terap AS UUID) LIMIT 1),
                id_atendimento AS (
                    SELECT id FROM atendimentos_grupo WHERE id_grupo_terapeutico IN (
                        SELECT id FROM id_grupo))
                INSERT INTO coordenacao_atendimento_grupo(id_funcionario, id_atendimento_grupo)
                SELECT id_func.id, id_atendimento.id 
                FROM id_func, id_atendimento
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("uid_funcionario", uidFuncionario)
                .bind("uid_grupo_terap", uidGrupoTerapeutico)
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .findFirst().orElseThrow(EntityNotFoundException::new));
    }
    // revisado
    @Override
    public GrupoTerapeutico addFicha(UUID uidFicha, UUID uidGrupoTerapeutico) {
        final String query = """
                WITH id_fic AS (
                    SELECT id FROM fichas WHERE uid = CAST(:uid_ficha AS UUID) LIMIT 1),
                id_grupo AS (
                    SELECT id FROM grupos_terapeuticos WHERE uid = CAST(:uid_grupo_terap AS UUID) LIMIT 1),
                id_atendimento AS (
                    SELECT id FROM atendimentos_grupo WHERE id_grupo_terapeutico IN (
                        SELECT id FROM id_grupo))
                INSERT INTO ficha_atendimento_grupo (id_ficha, id_atendimento_grupo)
                SELECT id_fic.id, id_atendimento.id
                FROM id_fic, id_atendimento
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("uid_ficha", uidFicha)
                .bind("uid_grupo_terap", uidGrupoTerapeutico)
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .findFirst().orElseThrow(EntityNotFoundException::new));
    }

    //revisado
    @Override
    public int delete(UUID uidGrupoTerapeutico) {
        final String Delete = """
                DELETE FROM grupos_terapeuticos WHERE uid = CAST(:uid AS UUID)
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(Delete)
                .bind("uid", uidGrupoTerapeutico)
                .execute());
    }

    // revisado
    @Override
    public int removerFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico) {
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE uid = CAST(:uid_funcionario AS UUID) LIMIT 1),
                id_grupo AS (
                    SELECT id FROM grupos_terapeuticos WHERE uid = CAST(:uid_grupo_terap AS UUID) LIMIT 1),
                id_atendimento AS (
                    SELECT id FROM atendimentos_grupo WHERE id_grupo_terapeutico = (
                        SELECT id FROM id_grupo))
                DELETE FROM coordenacao_atendimento_grupo WHERE 
                id_funcionario = (SELECT id FROM id_func) 
                AND id_atendimento_grupo = (SELECT id FROM id_atendimento)
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("uid_funcionario", uidFuncionario)
                .bind("uid_grupo_terap", uidGrupoTerapeutico)
                .execute());
    }

    @Override
    public int removerFicha(UUID uidFicha, UUID uidGrupo) {
        final String query = """
                WITH id_fic AS (
                    SELECT id FROM fichas WHERE uid = CAST (:uid_ficha AS UUID) LIMIT 1),
                id_grupo AS (
                    SELECT id FROM grupos_terapeuticos WHERE uid = CAST(:uid_grupo_terap AS UUID) LIMIT 1),
                id_atendimento AS (
                    SELECT id FROM atendimentos_grupo WHERE id_grupo_terapeutico = (
                        SELECT id FROM id_grupo))
                DELETE FROM ficha_atendimento_grupo WHERE 
                id_ficha = (SELECT id FROM id_fic) 
                AND id_atendimento_grupo = (SELECT id FROM id_atendimento) 
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("uid_ficha", uidFicha)
                .bind("uid_grupo_terap", uidGrupo)
                .execute());
    }

    @Override
    public int delete(List<UUID> uuids) {
        return 0;
    }
}
