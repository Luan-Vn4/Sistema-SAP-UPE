package br.upe.sap.sistemasapupe.data.repositories.jdbc;

import br.upe.sap.sistemasapupe.data.jdbcutils.updates.ColumnParameterSource;
import br.upe.sap.sistemasapupe.data.jdbcutils.queries.ColumnRowMapper;
import br.upe.sap.sistemasapupe.data.jdbcutils.updates.UpdateUtils;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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


    // CREATE

    private final String CREATEQUERY = """
        INSERT INTO funcionarios(nome, sobrenome, email, senha, imagem, is_tecnico, is_ativo) VALUES
            (:nome, :sobrenome, :email, :senha, :imagem, :is_tecnico, :is_ativo);
        """;

    @Override
    public Funcionario create(Funcionario funcionario) {
        namedJdbc.execute(CREATEQUERY,
                ColumnParameterSource.newInstance(Funcionario.class, funcionario),
                UpdateUtils.updateThenInjectGeneratedKeys(funcionario, "id", "uid"));

        return funcionario;
    }

    @Override
    public List<Funcionario> create(List<Funcionario> funcionarios) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.batchUpdate(CREATEQUERY,
                ColumnParameterSource.newInstances(Funcionario.class, funcionarios),
                keyHolder, new String[] {"id", "uid"});

        UpdateUtils.injectKeys(keyHolder, funcionarios);
        return funcionarios;
    }


    // UPDATE
    @Override
    public Funcionario updateSupervisionado(Funcionario estagiario, Funcionario novoSupervisor) {
        return null;
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
    public List<Funcionario> findSupervisionados(UUID uidTecnico) {
        return List.of();
    }

    @Override
    public List<Funcionario> findFuncionariosAtivos() {
        return List.of();
    }

    @Override
    public List<Funcionario> findTecnicos() {
        return List.of();
    }

    @Override
    public Funcionario findById(UUID uid) {
        String sql = "SELECT * FROM funcionarios WHERE uid = :uid";
        return namedJdbc.queryForObject(sql, Map.of("uid", uid),
                ColumnRowMapper.newInstance(Funcionario.class, Funcionario::new));
    }

    @Override
    public List<Funcionario> findAll() {
        String sql = "SELECT nome FROM funcionarios";
        return jdbc.query(sql, ColumnRowMapper.newInstance(Funcionario.class, Funcionario::new));
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
