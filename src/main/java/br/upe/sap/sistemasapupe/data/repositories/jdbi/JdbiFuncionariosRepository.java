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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
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
        if (estagiario.getSupervisor() == null) throw new IllegalArgumentException(
                "O estagiário deve ter um supervisor!");

        final String CREATE_ESTAGIARIO = createFuncionarioSQL(false);
        final String CREATE_SUPERVISAO = """
            INSERT INTO supervisoes (id_supervisor, id_estagiario)
                VALUES (:id_supervisor, :id_estagiario);
            """;

        return jdbi.withHandle(handle -> {
            Estagiario estagiarioResult = handle
                .createUpdate(CREATE_ESTAGIARIO)
                .bindBean(estagiario)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Estagiario.class)
                .findFirst().orElseThrow(EntityNotFoundException::new);

            Tecnico supervisor = estagiario.getSupervisor();

            if (supervisor.getId() == null) {
                estagiarioResult.setSupervisor(this.createTecnico(supervisor));
            } else {
                estagiarioResult.setSupervisor(supervisor);
            }

            handle.createUpdate(CREATE_SUPERVISAO)
                .bind("id_estagiario", estagiarioResult.getId())
                .bind("id_supervisor", estagiarioResult.getSupervisor().getId())
                .execute();

            return estagiarioResult;
        });
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
                SELECT id FROM funcionarios WHERE uid = :uidEstagiario LIMIT 1),
            id_sup AS (
                SELECT id FROM funcionarios WHERE uid = :uidSupervisor LIMIT 1)
            UPDATE supervisoes SET id_supervisor = (SELECT id FROM id_sup LIMIT 1)
                WHERE id_estagiario = (SELECT id FROM id_est LIMIT 1)
            RETURNING (SELECT * FROM estagiario_supervisor WHERE id_estagiario = 1 LIMIT 1)
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bind("uidEstagiario", uidEstagiario)
            .bind("uidSupervisor", uidSupervisor)
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> {
                var estagiario = BeanMapper.of(Estagiario.class, "est_").map(rs, ctx);
                var supervisor = BeanMapper.of(Tecnico.class, "sup_").map(rs, ctx);
                estagiario.setSupervisor(supervisor);
                return estagiario;
            })
            .findFirst().orElse(null));
    }

    @Override
    public Funcionario updateAtivo(UUID uidFuncionario, boolean isAtivo) {
        return null;
    }

    @Override
    public Funcionario update(Funcionario funcionario) {
        return null;
    }

    @Override
    public List<Funcionario> update(List<Funcionario> funcionarios) {
        return List.of();
    }


    // READ
    @Override
    @Nullable
    public Funcionario findById(UUID uid) {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo FROM funcionarios
                WHERE uid = :uid LIMIT 1;
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
    public List<Funcionario> findById(List<UUID> ids) {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo
                FROM funcionarios WHERE uid IN (:uidsList)
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("uidsList", ids)
            .map(this::mapByCargo)
            .collectIntoList());
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
        final String DELETE = """
            WITH r_id AS (
                SELECT id FROM funcionarios WHERE uid = :uid LIMIT 1
            )
            DELETE FROM funcionarios WHERE id = (SELECT id FROM r_id LIMIT 1);
            """;

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("uid", uid)
            .execute());
    }

    @Override
    public int delete(List<UUID> uuids) {
        return 0;
    }


}
