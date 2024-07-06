package br.upe.sap.sistemasapupe.data.repositories.jdbc;

import br.upe.sap.sistemasapupe.data.jdbcutils.updates.ColumnParameterSource;
import br.upe.sap.sistemasapupe.data.jdbcutils.queries.ColumnRowMapper;
import br.upe.sap.sistemasapupe.data.jdbcutils.updates.UpdateUtils;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;
import java.util.function.Supplier;

@Repository
public class JdbcFuncionarioRepository implements FuncionarioRepository {

    // ATRIBUTOS
    JdbcTemplate jdbc;

    NamedParameterJdbcTemplate namedJdbc;


    // CONSTRUTORES E MÉTODOS DE ACESSO
    public JdbcFuncionarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
    }


    // AUXILIARES
    private Map<String, Object> isTecnico(Funcionario funcionario) {
        return funcionario.getCargo().equals(Cargo.TECNICO) ? Map.of("is_tecnico", true) :
                Map.of("is_tecnico", false);
    }

    private Optional<Integer> getIdFromUid(UUID uid) {
        Object[] args = new Object[]{uid};
        int[] argsTypes = new int[]{Types.INTEGER};

        Integer result = jdbc.queryForObject(
                "SELECT id FROM funcionarios WHERE uid = ?", args, argsTypes, Integer.class);

        return Optional.ofNullable(result);
    }

    private Cargo isTecnicoToCargo(Object isTecnico) {
        if ((Boolean) isTecnico) return Cargo.TECNICO;
        return Cargo.ESTAGIARIO;
    }

    // CREATE
    private final String CREATEQUERY = """
        INSERT INTO funcionarios(nome, sobrenome, email, senha, imagem, is_tecnico, is_ativo) VALUES
            (:nome, :sobrenome, :email, :senha, :imagem, :is_tecnico, :is_ativo);
        """;

    @Override
    public Funcionario create(Funcionario funcionario) {
        Map<String, Object> params = isTecnico(funcionario);

        namedJdbc.execute(CREATEQUERY,
            ColumnParameterSource.newInstance(Funcionario.class, funcionario, params),
            UpdateUtils.updateThenInjectGeneratedKeys(funcionario, "id", "uid"));

        return funcionario;
    }

    @Override
    public List<Funcionario> create(List<Funcionario> funcionarios) {
        List<Map<String, Object>> params = funcionarios.stream().map(this::isTecnico).toList();

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.batchUpdate(CREATEQUERY,
                ColumnParameterSource.newInstances(Funcionario.class, funcionarios, params),
                keyHolder, new String[] {"id", "uid"});

        UpdateUtils.injectKeys(keyHolder, funcionarios);
        return funcionarios;
    }


    @Override
    public Estagiario createEstagiario(Estagiario estagiario) {
        final String CREATE = """
            INSERT INTO funcionarios(nome, sobrenome, email, senha, is_tecnico, imagem, is_ativo) VALUES
                (:nome, :sobrenome, :email, :senha, false, :imagem, :is_ativo);
            """;

        namedJdbc.execute(CREATE,
            ColumnParameterSource.newInstance(Funcionario.class, estagiario),
            UpdateUtils.updateThenInjectGeneratedKeys(Funcionario.class, "id", "uid")
        );

        final String UPDATE = """
            INSERT INTO supervisoes(id_supervisor, id_estagiario) VALUES
                (:id_supervisor, :id_estagiario);
            """;

        namedJdbc.update(UPDATE,
            Map.of("id_estagiario", estagiario.getId(),
                "id_supervisor", estagiario.getSupervisor().getId())
        );

        return estagiario;
    }

    public Tecnico createTecnico(Tecnico tecnico) {
        return (Tecnico) create(tecnico);
    }

    // UPDATE
    @Override
    public Estagiario updateSupervisao(UUID uidEstagiario, UUID uidSupervisor) {
        final String UPDATE = """
            UPDATE supervisoes SET id_supervisor = :uid_supervisor
                WHERE id_estagiario = :uid_estagiario
            """;

        namedJdbc.update(UPDATE,
                Map.of("uid_supervisor", uidSupervisor, "uid_estagiario", uidEstagiario));

        Estagiario estagiario = (Estagiario) findOrThrow(uidEstagiario,
                "UUID de estagiário " + uidEstagiario + " inválido", 1);

        Tecnico tecnico = (Tecnico) findOrThrow(uidSupervisor,
                "UUID de supervisor " + uidSupervisor + " inválido", 1);

        estagiario.setSupervisor(tecnico);

        return estagiario;
    }

    private Funcionario findOrThrow(UUID uid, String message, int nRows) {
        Funcionario funcionario = this.findById(uid);

        if (funcionario == null) throw new EmptyResultDataAccessException(message, nRows);
        return funcionario;
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
    public List<Estagiario> findSupervisionados(UUID uidTecnico) {
        return List.of();
    }

    @Override
    public List<Funcionario> findFuncionariosAtivos() {
        return List.of();
    }

    @Override
    public List<Tecnico> findTecnicos() {
        return List.of();
    }

    @Override
    public Funcionario findById(UUID uid) {
        String SQL = "SELECT * FROM funcionarios WHERE uid = :uid";
        Map<String, Object> args = Map.of("uid", uid);
        SqlRowSet rowSet = namedJdbc.queryForRowSet(SQL, args);

        if(rowSet.first()) return null;

        Class<?> clazz;
        Supplier<?> constructor;
        if (rowSet.getBoolean("is_tecnico")) {
            clazz = Tecnico.class;
            constructor = Tecnico::new;
        } else {
            clazz = Estagiario.class;
            constructor = Estagiario::new;
        }

        return instantiateFuncionario(Estagiario.class, Estagiario::new, rowSet, true);
    }

    private <T extends Funcionario> Funcionario instantiateFuncionario(Class<T> clazz, Supplier<T> cons,
                                                                       SqlRowSet rowSet, boolean restart) {
        return ColumnRowMapper.newInstance(clazz, cons,
                        Map.of("is_tecnico", this::isTecnicoToCargo))
                        .mapRowSet(rowSet, true).get(0);
    }

    @Override
    public List<Funcionario> findAll() {
        //String QUERY = "SELECT nome FROM funcionarios";
        return null;
    }

    @Override
    public List<Funcionario> findById(List<UUID> ids) {
        return List.of();
    }


    // DELETE
    @Override
    public void delete(UUID id) {

    }

    @Override
    public void delete(List<UUID> uuids) {

    }

}
