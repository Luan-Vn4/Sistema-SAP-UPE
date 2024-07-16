package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jdbi.v3.core.Jdbi;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    // procurar o grupo pelo id dele mesmo
    @Override
    public GrupoTerapeutico findById(UUID uid) {
        final String query = """
                SELECT * FROM grupos_terapeuticos
                WHERE uid = :uid
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
                .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findByFuncionario(UUID uidFuncionario) {
        // Não tenho certeza
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE uid = CAST(:uid AS UUID) LIMIT 1),
                id_participacao AS (
                    SELECT id_grupo_terapeutico AS id_grupo FROM participacao_grupo_terapeutico
                        WHERE id_funcionario = (SELECT id FROM id_func LIMIT 1))
                SELECT * FROM grupos_terapeuticos WHERE id IN (select id_grupo from id_participacao);
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public List<GrupoTerapeutico> findByFicha(UUID idFicha) {
        final String query = """
                WITH id_fic AS (
                    SELECT id FROM fichas WHERE uid = CAST(:uid AS UUID) LIMIT 1),
                id_atendimento AS (
                    SELECT id_atendimento_grupo AS id_grupo FROM ficha_atendimento_grupo 
                        WHERE id_ficha = (SELECT id FROM id_fic LIMIT 1))
                SELECT * FROM grupos_terapeuticos WHERE id = (SELECT id_grupo FROM id_atendimento)
                """;

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .mapToBean(GrupoTerapeutico.class)
                .collectIntoList());
    }

    @Override
    public GrupoTerapeutico addFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico) {
        final String query = """
                WITH id_func AS (
                    SELECT id FROM funcionarios WHERE uid = CAST(:uid_funcionario AS UUID) LIMIT 1),
                id_grupo AS (
                    SELECT id FROM grupos_terapeuticos WHERE uid = CAST(:uid_grupo AS UUID) LIMIT 1)
                INSERT INTO participacao_grupo_terapeutico(id_funcionario, id_grupo_terapeutico)
                VALUES ((SELECT id FROM id_func)), (SELECT id FROM id_grupo))
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("uid_funcionario", uidFuncionario)
                .bind("uid_grupo", uidGrupoTerapeutico)
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .findFirst().orElseThrow(EntityNotFoundException::new));
    }

    // não sei oq fazer aqui
    @Override
    public GrupoTerapeutico addFicha(UUID uidFicha, UUID uidGrupoTerapeutico) {
        return null;
    }

    public GrupoTerapeutico removeFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico) {
        return null;
    }

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

    @Override
    public int delete(List<UUID> uuids) {
        return 0;
    }
}
