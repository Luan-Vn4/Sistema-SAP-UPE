package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
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
                RETURNING %s
            """.formatted(isTecnico, returningColumns);
    }

    private final String returningColumns = "uid, id, nome, sobrenome, email, senha, is_tecnico tecnico, " +
            "url_imagem urlImagem, is_ativo ativo";

    @Override
    public Estagiario createEstagiario(Estagiario estagiario) {
        verifyIdsBeforeCreation(estagiario);
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

    private void verifyIdsBeforeCreation(Funcionario funcionario) {
        if (funcionario.getId() != null || funcionario.getUid() != null)
            throw new IllegalArgumentException(
                    "O funcionario fornecido não deve ter suas chaves preenchidas");
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
        verifyIdsBeforeCreation(tecnico);
        final String CREATE = createFuncionarioSQL(true);

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
            .bindBean(tecnico)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Tecnico.class)
            .findFirst().orElse(null));
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
    public Estagiario updateSupervisao(Integer idEstagiario, Integer idSupervisor) {
        final String UPDATE = """
            UPDATE supervisoes SET id_supervisor = :id_supervisor
                WHERE id_estagiario = :id_estagiario
            """;

        return jdbi.withHandle(handle -> {
            handle.createUpdate(UPDATE)
                .bind("id_estagiario", idEstagiario)
                .bind("id_supervisor", idSupervisor)
                .execute();

            return (Estagiario) this.findById(idEstagiario);
        });
    }

    @Override
    public boolean updateAtivo(Integer id, boolean isAtivo) {
        final String UPDATE = """
            UPDATE funcionarios SET is_ativo = :is_ativo WHERE id = :id
                RETURNING %s
            """.formatted(returningColumns);

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bind("is_ativo", isAtivo)
            .bind("id", id)
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> rs.getBoolean("ativo"))
            .findFirst().orElseThrow(() ->
                new EntityNotFoundException("Não existe funcionário com o id: " + id)));
    }

    private String createUpdateQuery(boolean isTecnico) {
        return """
            UPDATE funcionarios SET nome = :nome, sobrenome = :sobrenome, email = :email, senha = :senha,
                url_imagem = :urlImagem, is_tecnico = %s, is_ativo = :ativo
                WHERE id = :id AND uid = CAST(:uid AS UUID)
                    RETURNING %s
            """.formatted(isTecnico, returningColumns);
    }

    @Override
    public Funcionario update(Funcionario funcionario) {
        final String UPDATE = createUpdateQuery(funcionario instanceof Tecnico);

        return jdbi.withHandle(handle -> handle
            .createUpdate(UPDATE)
            .bindBean(funcionario)
            .executeAndReturnGeneratedKeys()
            .map(this::mapByCargo)
            .findFirst().orElseThrow(() ->
                new EntityNotFoundException("Não foi encontrado funcionário com id: "
                                            + funcionario.getId())));
    }

    @Override
    public List<Funcionario> update(List<Funcionario> funcionarios) {
        return jdbi.inTransaction(handle -> {
            List<Funcionario> result = new ArrayList<>();
            for (Funcionario funcionario : funcionarios) result.add(update(funcionario));
            return result;
        });
    }

    @Override
    public void updatePassword(Integer idFuncionario, String newPassword) {
        final String UPDATE = "UPDATE funcionarios SET senha = :password WHERE id = :idFuncionario";

        jdbi.useHandle(handle -> handle
            .createUpdate(UPDATE)
            .bind("idFuncionario",idFuncionario)
            .bind("password", newPassword)
            .execute());
    }

    // READ
    @Override
    public Funcionario findById(Integer id) {
        if (id == null) throw new IllegalArgumentException("id não deveria ser nulo");

        final String QUERY = """
            SELECT %s FROM funcionarios
                    WHERE id = :id LIMIT 1;
        """.formatted(returningColumns);

        Funcionario funcionario = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map(this::mapByCargo)
            .findFirst().orElse(null));

        setSupervisorIfEstagiario(funcionario);

        return funcionario;
    }

    private void setSupervisorIfEstagiario(Funcionario funcionario) {
        if (funcionario instanceof Estagiario estagiario) {
            estagiario.setSupervisor(findSupervisor(estagiario.getId()));
        }
    }

    private Funcionario mapByCargo(ResultSet rs, StatementContext ctx) throws SQLException {
        return rs.getBoolean("tecnico") ?
                BeanMapper.of(Tecnico.class).map(rs, ctx) :
                BeanMapper.of(Estagiario.class).map(rs, ctx);
    }

    @Override
    public List<Funcionario> findAll() {
        final String QUERY = "SELECT %s FROM funcionarios".formatted(returningColumns);

        List<Funcionario> results = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapByCargo)
            .collectIntoList());

        for (Funcionario funcionario : results) setSupervisorIfEstagiario(funcionario);

        return results;
    }

    @Override
    public List<Funcionario> findById(List<Integer> ids) {
        final String QUERY = "SELECT %s FROM funcionarios WHERE id IN (%s)"
            .formatted( returningColumns, "<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids", ids)
            .map(this::mapByCargo)
            .collectIntoList());
    }

    public boolean exists(Integer id) {
        final String QUERY = """
            SELECT COUNT(*) > 0 FROM funcionarios
                    WHERE id = :id GROUP BY id LIMIT 1
            """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
                .bind("id", id)
                .mapTo(Boolean.class)
                .findFirst().orElse(false));
    }

    @Override
    public List<Estagiario> findSupervisionados(Integer idTecnico) {
        Funcionario funcionario = findById(idTecnico);

        if (funcionario == null) throw new EntityNotFoundException("O funcionário fornecido não existe");
        if (funcionario instanceof Estagiario estagiario) throw new IllegalArgumentException(
                "O funcionário fornecido não é um técnico. ID: " + idTecnico);

        final String QUERY = """
            SELECT %s from funcionarios INNER JOIN
                supervisoes ON id = id_estagiario AND id_supervisor = :idTecnico
            """.formatted(returningColumns);

        return jdbi.withHandle(handle ->
            handle.createQuery(QUERY)
            .bind("idTecnico", idTecnico)
            .map((rs, ctx) -> (Estagiario) mapByCargo(rs, ctx))
            .collectIntoList());
    }

    @Nullable
    public Tecnico findSupervisor(Integer idEstagiario) {
        final String QUERY = """
           WITH supervisao AS (
               SELECT id_estagiario, id_supervisor FROM supervisoes
                   WHERE id_estagiario = :id LIMIT 1
           )
           SELECT id_estagiario, sp.id, sp.uid, sp.nome, sp.sobrenome, sp.email, sp.senha,
               sp.url_imagem urlImagem, sp.is_tecnico tecnico, sp.is_ativo ativo FROM supervisao INNER JOIN
                   (SELECT * FROM funcionarios) sp ON id = id_supervisor
           """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", idEstagiario)
            .map((rs, ctx) -> {
               if (rs.getInt("id_estagiario") == 0)
                   throw new EntityNotFoundException("Não foi possível identificar um " +
                                                     "estagiário com o id: " + idEstagiario);
               return BeanMapper.of(Tecnico.class).map(rs, ctx);
            }).findFirst().orElse(null));
    }

    @Override
    public List<Funcionario> findByAtivo(boolean ativo) {
        final String QUERY = "SELECT %s FROM funcionarios WHERE is_ativo = :ativo"
            .formatted(returningColumns);

        List<Funcionario> results = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("ativo", ativo)
            .map(this::mapByCargo)
            .collectIntoList());

        results.forEach(this::setSupervisorIfEstagiario);

        return results;
    }

    @Override
    public List<Tecnico> findTecnicos() {
        final String QUERY = "SELECT %s FROM funcionarios WHERE is_tecnico = TRUE"
            .formatted(returningColumns);

        return jdbi.withHandle(handle ->
            handle.createQuery(QUERY)
            .mapToBean(Tecnico.class)
            .collectIntoList());
    }

    @Override
    public Funcionario findByEmail(String email) {
        final String QUERY = "SELECT %s FROM funcionarios WHERE email = :email".formatted(returningColumns);

        Funcionario funcionario = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("email", email)
            .map(this::mapByCargo)
            .findFirst().orElse(null));

        setSupervisorIfEstagiario(funcionario);

        return funcionario;
    }

    @Override
    public BidiMap<UUID, Integer> findIds(UUID uuid) {
        final String SELECT = "SELECT uid, id FROM funcionarios WHERE uid = CAST(:uuid AS UUID) LIMIT 1";

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
        final String SELECT = "SELECT uid, id FROM funcionarios WHERE uid IN (%s) LIMIT %d"
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

    // DELETE
    @Override
    public int delete(Integer id) {
        final String DELETE = "DELETE FROM funcionarios WHERE id = :id";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("id", id)
            .execute());
    }

    @Override
    public int delete(List<Integer> ids) {
        final String DELETE = "DELETE FROM funcionarios WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("ids", ids)
            .execute());
    }


}
