package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbiFuncionariosRepository implements FuncionarioRepository {

    Jdbi jdbi;

    public JdbiFuncionariosRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    // CREATE
    private String createFuncionarioSQL(boolean isTecnico) {
        return """
            INSERT INTO funcionarios (nome, sobrenome, email, senha, is_tecnico, url_imagem, is_ativo) VALUES
                (:nome, :sobrenome, :email, :senha, %s, :urlImagem, :ativo)
                RETURNING *
            """.formatted(isTecnico);
    }

    @Override
    public Estagiario createEstagiario(Estagiario estagiario) {
        validateSupervisor(estagiario);

        final String CREATE_ESTAGIARIO = createFuncionarioSQL(false);
        final String CREATE_SUPERVISAO = """
            INSERT INTO supervisoes (id_supervisor, id_estagiario)
                VALUES (:id_supervisor, :id_estagiario);
            """;

        return jdbi.inTransaction(handle -> {
            Estagiario estagiarioResult = handle
                    .createUpdate(CREATE_ESTAGIARIO)
                    .bindBean(estagiario)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Estagiario.class)
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            estagiarioResult.setSupervisor(estagiario.getSupervisor());

            handle.createUpdate(CREATE_SUPERVISAO)
                    .bind("id_estagiario", estagiarioResult.getId())
                    .bind("id_supervisor", estagiarioResult.getSupervisor().getId())
                    .execute();

            return estagiarioResult;
        });
    }

    private void validateSupervisor(Estagiario estagiario) {
        Tecnico supervisor = estagiario.getSupervisor();

        if (supervisor == null) {
            throw new IllegalArgumentException("O estagiário deve ter um supervisor");
        } else if(supervisor.getId() == null) {
            throw new IllegalArgumentException("O supervisor não pode ter ID nulo");
        }

    }

    @Override
    public Tecnico createTecnico(Tecnico tecnico) {
        final String CREATE = createFuncionarioSQL(true);

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
            .bindBean(tecnico)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Tecnico.class))
            .first();
    }

    @Override
    public Funcionario create(Funcionario funcionario) {
        if (funcionario.getId() != null || funcionario.getUid() != null)
            throw new IllegalArgumentException(
                    "O funcionario fornecido não deve ter suas chaves preenchidas");
        return funcionario instanceof Tecnico ? createTecnico((Tecnico) funcionario) :
                                                createEstagiario((Estagiario) funcionario);
    }

    @Override
    public List<Funcionario> create(List<Funcionario> funcionarios) {
        return funcionarios.stream().map(this::create).toList();
    }


    // UPDATE
    @Override
    public Estagiario updateSupervisao(UUID uidEstagiario, UUID uidSupervisor) {
        final String UPDATE = """
            WITH id_est AS (
                SELECT id FROM funcionarios WHERE uid = cast(:uid_estagiario AS UUID) LIMIT 1),
            id_sup AS (
                SELECT id FROM funcionarios WHERE uid = cast(:uid_supervisor AS UUID) LIMIT 1)
            UPDATE supervisoes SET id_supervisor = (SELECT id FROM id_sup LIMIT 1)
                WHERE id_estagiario = (SELECT id FROM id_est LIMIT 1)
            """;

        return jdbi.withHandle(handle -> {
            handle.createUpdate(UPDATE)
                .bind("uid_estagiario", uidEstagiario)
                .bind("uid_supervisor", uidSupervisor)
                .execute();

            return (Estagiario) this.findById(uidEstagiario);
        });
    }

    @Override
    public boolean updateAtivo(UUID uidFuncionario, boolean isAtivo) {
        final String UPDATE = """
            UPDATE funcionarios SET is_ativo = :is_ativo WHERE uid = CAST(:uid AS UUID);
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bind("uid", uidFuncionario)
            .executeAndReturnGeneratedKeys())
            .mapTo(Boolean.class)
            .findFirst().orElseThrow(() ->
                new EntityNotFoundException("Não existe funcionário com o uid: " + uidFuncionario));
    }

    private String createUpdateQuery(boolean isTecnico) {
        return  """
            UPDATE funcionarios SET nome = :nome, sobrenome = :sobrenome, email = :email, senha = :senha,
                url_imagem = :urlImagem, is_tecnico = %s, is_ativo = :isAtivo
                WHERE id = :id AND uid = CAST(:uid AS UUID)
                    RETURNING nome, sobrenome, email, senha, url_imagem, is_ativo, is_tecnico
            """.formatted(isTecnico);
    }

    @Override
    public Funcionario update(Funcionario funcionario) {
        final String UPDATE = createUpdateQuery(funcionario instanceof Tecnico);

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bindBean(funcionario)
            .executeAndReturnGeneratedKeys())
            .map(this::mapByCargo)
            .findFirst().orElseThrow(() ->
                new EntityNotFoundException("Não foi encontrado funcionário com uid: "
                                            + funcionario.getUid()));
    }

    @Override
    public List<Funcionario> update(List<Funcionario> funcionarios) {
        return jdbi.inTransaction(handle -> {
            List<Funcionario> result = new ArrayList<>();
            for (Funcionario funcionario : funcionarios) result.add(update(funcionario));
            return result;
        });
    }


    // READ
    @Override
    @Nullable
    public Funcionario findById(UUID uid) {
        if (uid == null) throw new IllegalArgumentException("UID não deveria ser nulo");

        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo FROM funcionarios
                WHERE uid = CAST(:uid AS UUID) LIMIT 1;
        """;

        Optional<Funcionario> funcionario = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("uid", uid)
            .map(this::mapByCargo)
            .findFirst());

        if (funcionario.isPresent() && funcionario.get() instanceof Estagiario estagiario) {
            estagiario.setSupervisor(findSupervisor(estagiario.getUid()));
        }

        return funcionario.orElse(null);
    }

    private Funcionario mapByCargo(ResultSet rs, StatementContext ctx) throws SQLException {
        return rs.getBoolean("is_Tecnico") ?
                BeanMapper.of(Tecnico.class).map(rs, ctx) :
                BeanMapper.of(Estagiario.class).map(rs, ctx);
    }

    @Override
    public List<Funcionario> findAll() {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo
                FROM funcionarios
            """;

        List<Funcionario> results = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapByCargo)
            .collectIntoList());

        for (Funcionario funcionario : results) {
            if (funcionario instanceof Estagiario estagiario) {
                estagiario.setSupervisor(findSupervisor(estagiario.getUid()));
            }
        }

        return results;
    }

    @Override
    public List<Funcionario> findById(List<UUID> uids) {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo
                FROM funcionarios WHERE uid IN (%s)
            """.formatted("<uids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("uids", uids)
            .map(this::mapByCargo)
            .collectIntoList());
    }

    public boolean exists(UUID uid) {
        final String QUERY = """
            SELECT COUNT(*) > 0 FROM funcionarios
                    WHERE uid = CAST(:uid AS UUID) GROUP BY uid LIMIT 1
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
                .bind("uid", uid)
                .mapTo(Boolean.class)
                .findFirst().orElse(false));
    }

    @Override
    public List<Estagiario> findSupervisionados(UUID uidTecnico) {
        final String QUERY = """
            WITH inf_sup AS (
                SELECT id inf_sup_id, is_tecnico inf_sup_cargo FROM funcionarios WHERE uid = :uid LIMIT 1
            ),
            sup_est AS (
                SELECT inf_sup_id, inf_sup_cargo, id_estagiario FROM inf_sup LEFT JOIN supervisoes
                    ON inf_sup_id = id_supervisor LIMIT 10)
            SELECT inf_sup_id, inf_sup_cargo, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo
                FROM funcionarios RIGHT JOIN sup_est ON id_estagiario = id LIMIT 10
            """;

        return jdbi.withHandle(handle ->
            handle.createQuery(QUERY)
            .bind("uid", uidTecnico)
            .map((rs, ctx) -> {
                if (!rs.getBoolean("inf_sup_cargo"))
                    throw new IllegalArgumentException("O funcionário fornecido não é um técnico. UID - " + uidTecnico);
                return BeanMapper.of(Estagiario.class).map(rs, ctx);
            }).collectIntoList());
    }

    @Nullable
    public Tecnico findSupervisor(UUID uidEstagiario) {
        final String QUERY = """
           WITH est_id AS (
               SELECT id FROM funcionarios WHERE uid = :uid LIMIT 1
           ),
           supervisao AS (
               SELECT id_estagiario, id_supervisor FROM supervisoes
                   WHERE id_estagiario = (SELECT id FROM est_id) LIMIT 1
           )
           SELECT id_estagiario, sp.id, sp.uid, sp.nome, sp.sobrenome, sp.email, sp.senha,
               sp.url_imagem, sp.is_tecnico, sp.is_ativo FROM supervisao INNER JOIN
                   (SELECT * FROM funcionarios) sp ON id = id_supervisor
           """;

        return jdbi.withHandle(handle -> handle.createQuery(QUERY))
            .bind("uid", uidEstagiario)
            .map((rs, ctx) -> {
               if (rs.getInt("id_estagiario") == 0)
                   throw new EntityNotFoundException("Não foi possível identificar um " +
                                                     "estagiário com o uid: " + uidEstagiario);
               return BeanMapper.of(Tecnico.class).map(rs, ctx);
            }).findFirst().orElse(null);
    }

    @Override
    public List<Funcionario> findFuncionariosAtivos() {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo FROM funcionarios
                WHERE is_ativo = TRUE;
            """;

        List<Funcionario> results = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapByCargo)
            .collectIntoList());

        for (Funcionario funcionario : results) {
            if (funcionario instanceof Estagiario estagiario) {
                estagiario.setSupervisor(findSupervisor(estagiario.getUid()));
            }
        }

        return results;
    }

    @Override
    public List<Tecnico> findTecnicos() {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo FROM funcionarios
                WHERE is_tecnico = TRUE;
            """;

        return jdbi.withHandle(handle ->
            handle.createQuery(QUERY)
            .mapToBean(Tecnico.class)
            .collectIntoList());
    }


    // DELETE
    @Override
    public int delete(UUID uid) {
        final String DELETE = "DELETE FROM funcionarios WHERE uid = :uid";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("uid", uid)
            .execute());
    }

    @Override
    public int delete(List<UUID> uids) {
        final String DELETE = "DELETE FROM funcionarios WHERE uid IN (%s)".formatted("<uids>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("uids", uids)
            .execute());
    }


}
